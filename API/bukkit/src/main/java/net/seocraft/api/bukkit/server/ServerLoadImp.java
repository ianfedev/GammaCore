package net.seocraft.api.bukkit.server;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.server.model.Server;
import net.seocraft.api.bukkit.server.model.ServerType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;

public class ServerLoadImp implements ServerLoad {

    @Inject private BukkitAPI instance;
    @Inject private ServerManager serverManager;

    public void setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {

        FileConfiguration configuration = this.instance.getConfig();
        int maxRunning = configuration.getInt("game.maxRunning");
        int maxTotal = configuration.getInt("game.maxTotal");

        //TODO: Find if gamemode/subgamemode exists

        try {
            ServerType type = ServerType.valueOf(this.instance.getConfig().getString("api.type"));

            Server server = this.serverManager.loadServer(
                    /* TODO: Get slug from */ "test-1",
                    type,
                    null,
                    null,
                    maxRunning,
                    maxTotal,
                    configuration.getString("game.cluster")
            );

            Bukkit.getLogger().log(Level.INFO, "[API-Bukkit] ServerImp connected to the API. (ID: {0})",
                    this.instance.getServerRecord().id());

        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Server type not found, shutting down this.instance.");
            Bukkit.getServer().shutdown();
        }
    }
}
