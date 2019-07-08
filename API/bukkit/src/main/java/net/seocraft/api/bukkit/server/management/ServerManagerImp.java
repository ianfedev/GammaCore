package net.seocraft.api.bukkit.server.management;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.model.Gamemode;
import net.seocraft.api.bukkit.game.subgame.SubGamemode;
import net.seocraft.api.bukkit.server.model.Server;
import net.seocraft.api.bukkit.server.model.ServerImp;
import net.seocraft.api.bukkit.server.model.ServerType;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.redis.RedisClient;
import net.seocraft.api.shared.serialization.JsonUtils;
import net.seocraft.api.shared.serialization.model.ModelSerializationHandler;
import net.seocraft.api.shared.server.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

public class ServerManagerImp implements ServerManager {

    @Inject private ServerConnectRequest serverConnectRequest;
    @Inject private ListeningExecutorService executorService;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private ServerGetRequest serverGetRequest;
    @Inject private ServerDisconnectRequest serverDisconnectRequest;
    @Inject private ServerUpdateRequest serverUpdateRequest;
    @Inject private ServerGetQueryRequest serverGetQueryRequest;
    @Inject private RedisClient redisClient;
    @Inject private JsonUtils parser;
    @Inject private ModelSerializationHandler modelSerializationHandler;

    @Override
    public @NotNull Server loadServer(@NotNull String slug, @NotNull ServerType serverType, @Nullable Gamemode gamemode, @Nullable SubGamemode subGamemode, int maxRunning, int maxTotal, @NotNull String cluster) throws Unauthorized, BadRequest, NotFound, InternalServerError {

        Server preServer = new ServerImp(
                UUID.randomUUID().toString(),
                slug,
                serverType,
                gamemode,
                subGamemode,
                maxRunning,
                maxTotal,
                0,
                new ArrayList<>(),
                cluster,
                new ArrayList<>()
        );

        String serializedServer = this.modelSerializationHandler.serializeModel(preServer, Server.class);

        String rawResponse = this.serverConnectRequest.executeRequest(
            serializedServer
        );

        Server responseServer = this.modelSerializationHandler.deserializeModel(
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

        return this.modelSerializationHandler.deserializeModel(
                rawResponse,
                Server.class
        );
    }

    @Override
    public @NotNull Server updateServer(@NotNull Server server) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String rawResponse = this.serverUpdateRequest.executeRequest(
                server.id(),
                this.modelSerializationHandler.serializeModel(server, Server.class),
                this.serverTokenQuery.getToken()
        );

        return this.modelSerializationHandler.deserializeModel(rawResponse, Server.class);
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
            object.addProperty("subgamemode", subGamemode);
        }

        String rawResponse = this.serverGetQueryRequest.executeRequest(
                object.toString(),
                this.serverTokenQuery.getToken()
        );

        return this.modelSerializationHandler.deserializeModel(
                rawResponse,
                new TypeToken<List<ServerImp>>(){}.getType()
        );
    }

    @Override
    public void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String token = this.serverTokenQuery.getToken();
        this.serverDisconnectRequest.executeRequest(token);
    }
}
