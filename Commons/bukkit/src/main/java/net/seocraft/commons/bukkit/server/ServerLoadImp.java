package net.seocraft.commons.bukkit.server;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.server.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class ServerLoadImp implements ServerLoad {

    @Inject private BukkitAPI instance;
    @Inject private GamemodeProvider gamemodeHandler;
    @Inject private ServerManager serverManager;

    public Server setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {

        FileConfiguration configuration = this.instance.getConfig();
        int maxRunning = configuration.getInt("game.maxRunning");
        int maxTotal = configuration.getInt("game.maxTotal");

        try {
            ServerType type = ServerType.valueOf(this.instance.getConfig().getString("api.type"));

            Gamemode gamemode;
            if (type == ServerType.GAME) {
                 gamemode = this.gamemodeHandler.getGamemodeSync(
                        configuration.getString("game.gamemode")
                 );
                 if (gamemode == null) return null;
                 List<SubGamemode> subGamemodes = gamemode.getSubGamemodes();

                 Optional<SubGamemode> subGamemode = subGamemodes
                         .stream()
                         .filter(
                                 s -> s.id().equalsIgnoreCase(configuration.getString("game.subgamemode"))
                         )
                         .findFirst();

                 if (!subGamemode.isPresent()) throw new NotFound("Sub Gamemode not found");

                 return this.serverManager.loadServer(
                         /* TODO: Get slug from */ "test-1",
                         type,
                         gamemode,
                         subGamemode.get(),
                         maxRunning,
                         maxTotal,
                         configuration.getString("api.cluster")
                 );
            }

            return this.serverManager.loadServer(
                    /* TODO: Get slug from */ "test-1",
                    type,
                    null,
                    null,
                    maxRunning,
                    maxTotal,
                    configuration.getString("api.cluster")
            );

        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Server type not found, shutting down this.instance.");
            Bukkit.getServer().shutdown();
        }
        return null;
    }

    public void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String token = this.serverTokenQuery.getToken();
        this.redisClient.deleteHash("authorization", this.bukkitAPI.getServerRecord().id());
        this.serverDisconnectRequest.executeRequest(token);
    }
}
