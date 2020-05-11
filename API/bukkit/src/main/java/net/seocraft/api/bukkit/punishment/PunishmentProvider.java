package net.seocraft.api.bukkit.punishment;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public interface PunishmentProvider {

    @NotNull
    Punishment createPunishment(@NotNull PunishmentType punishmentType,
                                @NotNull User issuer,
                                @NotNull User punished,
                                @NotNull String server,
                                @Nullable Match match,
                                @NotNull String lastIp,
                                @NotNull String reason,
                                long expiration,
                                boolean automatic,
                                boolean silent) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    ListenableFuture<AsyncResponse<Punishment>> getPunishmentById(@NotNull String id);

    @Nullable Punishment getPunishmentByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull ListenableFuture<AsyncResponse<Punishment>> getLastPunishment(@Nullable PunishmentType type, @NotNull String playerId);

    @Nullable Punishment getLastPunishmentSync(@Nullable PunishmentType type, @NotNull String playerId) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull ListenableFuture<AsyncResponse<List<Punishment>>> getPunishments(@Nullable PunishmentType type, @Nullable String playerId, boolean active);

    @Nullable List<Punishment> getPunishmentsSync(@Nullable PunishmentType type, @Nullable String playerId, boolean active) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull ListenableFuture<AsyncResponse<Punishment>> updatePunishment(@NotNull Punishment punishment);

    @NotNull Punishment updatePunishmentSync(@NotNull Punishment punishment) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

}