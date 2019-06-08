package net.seocraft.commons.bukkit.punishment;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.model.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PunishmentHandler {

    @NotNull Punishment createPunishment(@NotNull PunishmentType punishmentType,
                                         @NotNull String punisher,
                                         @NotNull String punished,
                                         @NotNull String server,
                                         @Nullable Match match,
                                         @NotNull String lastIp,
                                         @NotNull String reason,
                                         long expiration,
                                         boolean automatic,
                                         boolean silent) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    ListenableFuture<AsyncResponse<Punishment>> getPunishmentById(@NotNull String id);

    @Nullable Punishment getPunishmentByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<Punishment>> getLastPunishment(@Nullable PunishmentType type, @NotNull String playerId);

    @Nullable Punishment getLastPunishmentSync(@Nullable PunishmentType type, @NotNull String playerId) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<List<Punishment>>> getPunishments(@Nullable PunishmentType type, @Nullable String playerId, boolean active);

    @Nullable List<Punishment> getPunishmentsSync(@Nullable PunishmentType type, @Nullable String playerId, boolean active) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<Punishment>> updatePunishment(@NotNull Punishment punishment);

    @NotNull Punishment updatePunishmentSync(@NotNull Punishment punishment) throws Unauthorized, BadRequest, NotFound, InternalServerError;

}