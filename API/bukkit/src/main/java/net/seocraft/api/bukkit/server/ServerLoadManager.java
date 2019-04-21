package net.seocraft.api.bukkit.server;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.Server;
import net.seocraft.api.shared.redis.RedisClient;
import net.seocraft.api.shared.serialization.JsonUtils;
import net.seocraft.api.shared.server.ServerConnectRequest;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

@Singleton
public class ServerLoadManager {

    private BukkitAPI instance = BukkitAPI.getInstance();
    private JsonUtils parser;
    private Gson gson;
    private RedisClient redis;
    private ServerConnectRequest connectRequest;

    @Inject
    public ServerLoadManager(JsonUtils parser, ServerConnectRequest connectRequest, RedisClient redis, Gson gson) {
        this.parser = parser;
        this.connectRequest = connectRequest;
        this.gson = gson;
        this.redis = redis;
    }

    public void setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {
        Server setupServer = new Server(UUID.randomUUID().toString());
        // TODO: Setup server slug with Cloud API setupServer.setSlug();
        setupServer.setSlug("test-1");
        setupServer.setCluster(this.instance.getConfig().getString("api.cluster"));
        setupServer.setStarted_at("" + (System.currentTimeMillis() / 1000L));
        setupServer.setPlayers(new ArrayList<>());
        try {
            setupServer.setType(Server.Type.valueOf(this.instance.getConfig().getString("api.type")));
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Server type not found, shutting down this.instance.");
            Bukkit.getServer().shutdown();
        }
        if (setupServer.getType().equals(Server.Type.GAME)) {
            // TODO: Game-API Implementation
        } else {
            String request = connectRequest.executeRequest(setupServer, this.instance.getPreSelectedMap());
            this.instance.setServerRecord(
                    gson.fromJson(this.parser.parseObject(request, "server").toString(), Server.class)
            );
            this.redis.setHash("authorization", setupServer.getSlug(), parser.parseJson(request, "token").getAsString());
            Bukkit.getLogger().log(Level.INFO, "[API-Bukkit] Server connected to the API. (ID: {0})",
                    this.instance.getServerRecord().id());
        }
    }
}
