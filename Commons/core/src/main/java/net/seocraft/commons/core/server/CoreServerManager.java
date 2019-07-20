package net.seocraft.commons.core.server;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.gson.JsonObject
import com.google.inject.Inject;
import net.seocraft.commons.core.backend.http.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.server.*;
import net.seocraft.api.core.server.ServerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CoreServerManager implements ServerManager {

    @Inject private ServerConnectRequest serverConnectRequest;
    @Inject private Gson gson;
    @Inject private ListeningExecutorService executorService;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private ServerGetRequest serverGetRequest;
    @Inject private ServerDisconnectRequest serverDisconnectRequest;
    @Inject private ServerUpdateRequest serverUpdateRequest;
    @Inject private ServerGetQueryRequest serverGetQueryRequest;
    @Inject private RedisClient redisClient;
    @Inject private JsonUtils parser;

    @Override
    public @NotNull Server loadServer(@NotNull String slug, @NotNull ServerType serverType, @Nullable Gamemode gamemode, @Nullable SubGamemode subGamemode, int maxRunning, int maxTotal, @NotNull String cluster) throws Unauthorized, BadRequest, NotFound, InternalServerError {

        Server preServer;
        if (gamemode != null && subGamemode != null && serverType == ServerType.GAME) {
            preServer = new CoreServer(
                    UUID.randomUUID().toString(),
                    slug,
                    serverType,
                    gamemode.id(),
                    subGamemode.id(),
                    maxRunning,
                    maxTotal,
                    0,
                    new ArrayList<>(),
                    cluster,
                    new ArrayList<>()
            );
        } else {
            preServer = new CoreServer(
                    UUID.randomUUID().toString(),
                    slug,
                    serverType,
                    null,
                    null,
                    maxRunning,
                    maxTotal,
                    0,
                    new ArrayList<>(),
                    cluster,
                    new ArrayList<>()
            );
        }

        String serializedServer = this.gson.toJson(preServer, Server.class);

        String rawResponse = this.serverConnectRequest.executeRequest(
            serializedServer
        );

        Server responseServer = this.gson.fromJson(
                this.parser.parseJson(
                        rawResponse,
                        "server"
                ).toString(),
                Server.class
        );

        this.redisClient.setHash(
                "authorization",
                responseServer.id(),
                this.parser.parseJson(
                        rawResponse,
                        "token"
                ).getAsString()
        );
        return responseServer;
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Server>> getServer(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS,getServerSync(id));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @Nullable Server getServerSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String rawResponse = this.serverGetRequest.executeRequest(
                id,
                this.serverTokenQuery.getToken()
        );

        return this.gson.fromJson(
                rawResponse,
                Server.class
        );
    }

    @Override
    public @NotNull Server updateServer(@NotNull Server server) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String rawResponse = this.serverUpdateRequest.executeRequest(
                server.id(),
                this.gson.toJson(server, Server.class),
                this.serverTokenQuery.getToken()
        );

        return this.gson.fromJson(rawResponse, Server.class);
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<List<Server>>> getServerByQuery(@Nullable String id, @Nullable String match, @Nullable String gamemode, @Nullable String subGamemode) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getServerByQuerySync(id, match, gamemode, subGamemode));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull List<Server> getServerByQuerySync(@Nullable String id, @Nullable String match, @Nullable String gamemode, @Nullable String subGamemode) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        JsonObject object = new JsonObject();
        if (id != null) object.addProperty("_id", id);
        if (match != null) object.addProperty("matches", match);
        if (gamemode != null) {
            object.addProperty("gamemode", gamemode);
            if (subGamemode == null) throw new IllegalArgumentException("You can not send a gamemode without sub-gamemode.");
            object.addProperty("sub_gamemode", subGamemode);
        }
        if (id == null && match == null && gamemode == null) throw new IllegalArgumentException("No query specified.");

        String rawResponse = this.serverGetQueryRequest.executeRequest(
                object.toString(),
                this.serverTokenQuery.getToken()
        );

        return this.gson.fromJson(
                rawResponse,
                new TypeToken<List<Server>>(){}.getType()
        );
    }

    @Override
    public void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String token = this.serverTokenQuery.getToken();
        this.redisClient.deleteHash("authorization", this.bukkitAPI.getServerRecord().id());
        this.serverDisconnectRequest.executeRequest(token);
    }
}
