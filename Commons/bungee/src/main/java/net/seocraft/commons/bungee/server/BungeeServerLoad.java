package net.seocraft.commons.bungee.server;

import com.google.inject.Inject;
import net.md_5.bungee.config.Configuration;
import net.seocraft.api.bungee.BungeeAPI;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.*;
import net.seocraft.commons.bungee.CommonsBungee;
import net.seocraft.commons.core.backend.server.ServerDisconnectRequest;

import java.io.IOException;
import java.util.logging.Level;

public class BungeeServerLoad implements ServerLoad {

    @Inject private CommonsBungee instance;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private ServerManager serverManager;
    @Inject private BungeeAPI bungeeAPI;
    @Inject private ServerDisconnectRequest serverDisconnectRequest;
    @Inject private RedisClient redisClient;

    @Override
    public Server setupServer() throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        Configuration configuration = this.bungeeAPI.getConfig();

        ServerType type;
        try {
            type = ServerType.valueOf(configuration.getString("api.type"));
            if (type != ServerType.BUNGEE) throw new IllegalArgumentException("Server type was not set to BUNGEE");
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            this.instance.getLogger().log(Level.SEVERE, "[API-Bungee] Server type was invalid / not set to BUNGEE mode, shutting down instance.");
            this.instance.getProxy().stop();
            return null;
        }

        return this.serverManager.loadServer(
                this.instance.getProxy().getName(),
                type,
                null,
                null,
                0,
                0,
                configuration.getString("api.cluster")
        );

    }

    @Override
    public void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String token = this.serverTokenQuery.getToken();
        this.redisClient.deleteHash("authorization", this.instance.getServerRecord().getId());
        this.serverDisconnectRequest.executeRequest(token);
    }
}
