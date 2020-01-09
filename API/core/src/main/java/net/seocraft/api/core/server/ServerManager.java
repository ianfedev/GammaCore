package net.seocraft.api.core.server;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public interface ServerManager {

    @NotNull Server loadServer(
            @NotNull String slug,
            @NotNull ServerType serverType,
            @Nullable String gamemode,
            @Nullable String subGamemode,
            int maxRunning,
            int maxTotal,
            @NotNull String cluster
    ) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull ListenableFuture<AsyncResponse<Server>> getServer(@NotNull String id);

    @Nullable Server getServerSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull Server updateServer(@NotNull Server server) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull ListenableFuture<AsyncResponse<Set<Server>>> getServerByQuery(
            @Nullable String id,
            @Nullable String match,
            @Nullable String gamemode,
            @Nullable String subgamemode,
            @Nullable String slug
    );

    @NotNull Set<Server> getServerByQuerySync(
            @Nullable String id,
            @Nullable String match,
            @Nullable String gamemode,
            @Nullable String subgamemode,
            @Nullable String slug
    ) throws Unauthorized, IllegalStateException, BadRequest, NotFound, InternalServerError, IOException;
}
