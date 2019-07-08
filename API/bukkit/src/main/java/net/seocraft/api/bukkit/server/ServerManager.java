package net.seocraft.api.bukkit.server;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.bukkit.game.gamemode.model.Gamemode;
import net.seocraft.api.bukkit.game.subgame.SubGamemode;
import net.seocraft.api.bukkit.server.model.Server;
import net.seocraft.api.bukkit.server.model.ServerType;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
public interface ServerManager {

    @NotNull Server loadServer(
            @NotNull String slug,
            @NotNull ServerType serverType,
            @Nullable Gamemode gamemode,
            @Nullable SubGamemode subGamemode,
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
    ) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    void disconnectServer(@NotNull String id);
}
