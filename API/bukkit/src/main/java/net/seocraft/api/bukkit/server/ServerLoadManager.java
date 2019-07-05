package net.seocraft.api.bukkit.server;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.server.model.ServerImp;
import net.seocraft.api.bukkit.server.model.ServerType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.logging.Level;

public class ServerLoadManager {

    @Inject private BukkitAPI instance;
    @Inject private ServerManager serverManager;

    public void setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {

        FileConfiguration configuration = this.instance.getConfig();
        ServerType type;
        Integer maxRunning = configuration.get("");

        //TODO: Find if gamemode/subgamemode exists

        try {
            type = ServerType.valueOf(this.instance.getConfig().getString("api.type"));
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Server type not found, shutting down this.instance.");
            Bukkit.getServer().shutdown();
        }

        this.serverManager.loadServer(
                /* TODO: Get slug from */ "test-1",
                type,
                "",
                "",
                "",
                "",
                ""
        );
        setupServer.setSlug("test-1");
        setupServer.setCluster(this.instance.getConfig().getString("api.cluster"));
        setupServer.setStartedAt("" + (System.currentTimeMillis() / 1000L));
        setupServer.setPlayers(new ArrayList<>());

        if (setupServer.getType().equals(ServerImp.Type.GAME)) {
            // TODO: Game-API Implementation
        } else {
            String request = connectRequest.executeRequest(setupServer, this.instance.getPreSelectedMap());
            this.instance.setServerRecord(
                    gson.fromJson(this.parser.parseObject(request, "server").toString(), ServerImp.class)
            );
            this.redis.setHash("authorization", setupServer.getSlug(), parser.parseJson(request, "token").getAsString());
            Bukkit.getLogger().log(Level.INFO, "[API-Bukkit] ServerImp connected to the API. (ID: {0})",
                    this.instance.getServerRecord().id());
        }
    }
}
