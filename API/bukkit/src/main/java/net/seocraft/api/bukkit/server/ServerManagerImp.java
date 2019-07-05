package net.seocraft.api.bukkit.server;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
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
import net.seocraft.api.shared.serialization.model.ModelSerializationHandler;
import net.seocraft.api.shared.server.ServerConnectRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerManagerImp implements ServerManager {

    @Inject private ServerConnectRequest serverConnectRequest;
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

        String rawResponse = this.serverConnectRequest.executeRequest(
            this.modelSerializationHandler.serializeModel(preServer, Server.class)
        );

        return this.modelSerializationHandler.deserializeModel(rawResponse, Server.class);
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Server>> getServer(@NotNull String id) {
        return null;
    }

    @Override
    public @Nullable Server getServerSync(@NotNull String id) {
        return null;
    }

    @Override
    public @NotNull Server updateServer(@NotNull Server server) {
        return null;
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<List<Server>>> getServerByQuery(@Nullable String id, @Nullable String match, @Nullable Map<String, String> gamemode) {
        return null;
    }

    @Override
    public @NotNull List<Server> getServerByQuerySync(@Nullable String id, @Nullable String match, @Nullable Map<String, String> gamemode) {
        return null;
    }

    @Override
    public void disconnectServer(@NotNull String id) {

    }
}
