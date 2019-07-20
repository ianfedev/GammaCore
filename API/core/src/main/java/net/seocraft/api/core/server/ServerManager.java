package net.seocraft.api.core.server;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ServerManager {

    @NotNull Server loadServer(
            @NotNull String slug,
            @NotNull ServerType serverType,
            @Nullable String gamemode,
            @Nullable String subGamemode,
            int maxRunning,
            int maxTotal,
            @NotNull String cluster
    ) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<Server>> getServer(@NotNull String id);

    @Nullable Server getServerSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull Server updateServer(@NotNull Server server) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<List<Server>>> getServerByQuery(
            @Nullable String id,
            @Nullable String match,
            @Nullable String gamemode,
            @Nullable String subgamemode
    );

    @NotNull List<Server> getServerByQuerySync(
            @Nullable String id,
            @Nullable String match,
            @Nullable String gamemode,
            @Nullable String subgamemode
    ) throws Unauthorized, IllegalStateException, BadRequest, NotFound, InternalServerError;

    void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError;
}
