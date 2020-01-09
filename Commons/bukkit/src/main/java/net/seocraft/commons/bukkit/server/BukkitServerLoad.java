package net.seocraft.commons.bukkit.server;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.*;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.backend.match.MatchCleanupRequest;
import net.seocraft.commons.core.backend.server.ServerDisconnectRequest;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public class BukkitServerLoad implements ServerLoad {

    @Inject private CommonsBukkit instance;
    @Inject private BukkitAPI api;
    @Inject private GamemodeProvider gamemodeHandler;
    @Inject private ServerManager serverManager;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private ServerDisconnectRequest serverDisconnectRequest;
    @Inject private MatchCleanupRequest matchCleanupRequest;
    @Inject private CoreGameManagement coreGameManagement;
    @Inject private RedisClient redisClient;

    public Server setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {

        FileConfiguration configuration = this.api.getConfig();
        int maxRunning = configuration.getInt("game.maxRunning");
        int maxTotal = configuration.getInt("game.maxTotal");

        ServerType type;
        try {
            type = ServerType.valueOf(configuration.getString("api.type"));
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Server type not found, shutting down instance.");
            Bukkit.getServer().shutdown();
            return null;
        }

        try {

            Gamemode gamemode;
            if (type == ServerType.GAME || type == ServerType.LOBBY) {
                gamemode = this.gamemodeHandler.getGamemodeSync(
                        configuration.getString("game.gamemode")
                );
                if (gamemode == null) return null;
                Set<SubGamemode> subGamemodes = gamemode.getSubGamemodes();

                if (type == ServerType.GAME) {
                    Optional<SubGamemode> subGamemode = subGamemodes
                            .stream()
                            .filter(
                                    s -> s.getId().equalsIgnoreCase(configuration.getString("game.subgamemode"))
                            )
                            .findFirst();
                    if (!subGamemode.isPresent()) throw new NotFound("Sub Gamemode not found");

                    this.coreGameManagement.initializeGameCore(gamemode, subGamemode.get());

                    return this.serverManager.loadServer(
                            Bukkit.getServerName(),
                            type,
                            gamemode.getId(),
                            subGamemode.get().getId(),
                            maxRunning,
                            maxTotal,
                            configuration.getString("api.cluster")
                    );

                }

                return this.serverManager.loadServer(
                        Bukkit.getServerName(),
                        type,
                        gamemode.getId(),
                        null,
                        maxRunning,
                        maxTotal,
                        configuration.getString("api.cluster")
                );
            }

            return this.serverManager.loadServer(
                    Bukkit.getServerName(),
                    type,
                    null,
                    null,
                    maxRunning,
                    maxTotal,
                    configuration.getString("api.cluster")
            );

        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Error while creating server.");
            ex.printStackTrace();
            Bukkit.getServer().shutdown();
        }
        return null;
    }

    public void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String token = this.serverTokenQuery.getToken();
        this.redisClient.deleteHash("authorization", this.instance.getServerRecord().getId());
        this.redisClient.clearHash("scheduledStarts:" + this.instance.getServerRecord().getId());
        if (this.instance.getServerRecord().getServerType() == ServerType.GAME) this.matchCleanupRequest.executeRequest(token);
        this.serverDisconnectRequest.executeRequest(token);
    }
}
