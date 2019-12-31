package net.seocraft.commons.core.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.commons.core.backend.server.ServerConnectRequest;
import net.seocraft.commons.core.backend.server.ServerGetQueryRequest;
import net.seocraft.commons.core.backend.server.ServerGetRequest;
import net.seocraft.commons.core.backend.server.ServerUpdateRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CoreServerManager implements ServerManager {

    @Inject private ServerConnectRequest serverConnectRequest;
    @Inject private ObjectMapper mapper;
    @Inject private ListeningExecutorService executorService;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private ServerGetRequest serverGetRequest;
    @Inject private ServerUpdateRequest serverUpdateRequest;
    @Inject private ServerGetQueryRequest serverGetQueryRequest;
    @Inject private RedisClient redisClient;

    @Override
    public @NotNull Server loadServer(@NotNull String slug, @NotNull ServerType serverType, @Nullable String gamemode, @Nullable String subGamemode, int maxRunning, int maxTotal, int maxPlayers, @NotNull String cluster) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {

        Server preServer;
        if (gamemode != null && subGamemode != null && serverType == ServerType.GAME) {
            preServer = new CoreServer(
                    UUID.randomUUID().toString(),
                    slug,
                    serverType,
                    gamemode,
                    subGamemode,
                    maxRunning,
                    maxTotal,
                    0,
                    new HashSet<>(),
                    maxPlayers,
                    cluster,
                    new HashSet<>()
            );
        } else if (serverType == ServerType.LOBBY) {
            preServer = new CoreServer(
                    UUID.randomUUID().toString(),
                    slug,
                    serverType,
                    gamemode,
                    null,
                    maxRunning,
                    maxTotal,
                    0,
                    new HashSet<>(),
                    maxPlayers,
                    cluster,
                    new HashSet<>()
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
                    new HashSet<>(),
                    maxPlayers,
                    cluster,
                    new HashSet<>()
            );
        }

        String serializedServer = this.mapper.writeValueAsString(preServer);
        JsonNode response = this.mapper.readTree(this.serverConnectRequest.executeRequest(
                serializedServer
        ));

        Server responseServer = this.mapper.readValue(
                this.mapper.writeValueAsString(
                        response.get("server")
                ),
                Server.class
        );

        this.redisClient.setHash(
                "authorization",
                responseServer.getId(),
                response.get("token").asText()
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
    public @Nullable Server getServerSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        String rawResponse = this.serverGetRequest.executeRequest(
                id,
                this.serverTokenQuery.getToken()
        );

        return this.mapper.readValue(
                rawResponse,
                Server.class
        );
    }

    @Override
    public @NotNull Server updateServer(@NotNull Server server) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        String rawResponse = this.serverUpdateRequest.executeRequest(
                server.getId(),
                this.mapper.writeValueAsString(server),
                this.serverTokenQuery.getToken()
        );

        return this.mapper.readValue(rawResponse, Server.class);
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Set<Server>>> getServerByQuery(@Nullable String id, @Nullable String match, @Nullable String gamemode, @Nullable String subGamemode, @Nullable String slug) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getServerByQuerySync(id, match, gamemode, subGamemode, slug));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull Set<Server> getServerByQuerySync(@Nullable String id, @Nullable String match, @Nullable String gamemode, @Nullable String subGamemode, @Nullable String slug) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        ObjectNode node = mapper.createObjectNode();
        if (id != null) node.put("_id",  id);
        if (match != null) node.put("matches", match);
        if (slug != null) node.put("slug", slug);
        if (gamemode != null) {
            node.put("gamemode", gamemode);
            if (subGamemode == null) throw new IllegalArgumentException("You can not send a gamemode without sub-gamemode.");
            node.put("sub_gamemode", subGamemode);
        }
        if (id == null && match == null && gamemode == null && slug == null) throw new IllegalArgumentException("No query specified.");

        String rawResponse = this.serverGetQueryRequest.executeRequest(
                mapper.writeValueAsString(node),
                this.serverTokenQuery.getToken()
        );

        return this.mapper.readValue(rawResponse, new TypeReference<Set<Server>>(){});
    }
}
