package net.seocraft.commons.bukkit.punishment;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.Match;
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
                                         @NotNull String evidence,
                                         long expiration,
                                         boolean automatic,
                                         boolean silent) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    ListenableFuture getPunishmentById(@NotNull String id);

    @Nullable Punishment getPunishmentByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<Punishment>> getLastPunishment(@NotNull PunishmentType type, @NotNull String playerId);

    @Nullable Punishment getLastPunishmentSync(@NotNull PunishmentType type, @NotNull String playerId);

    ListenableFuture<AsyncResponse<List<Punishment>>> getPunishments(@Nullable PunishmentType type, @Nullable String playerId, boolean active);

    List<Punishment> getPunishmentsSync(@Nullable PunishmentType type, @Nullable String playerId, boolean active);

    @NotNull ListenableFuture<AsyncResponse<Punishment>> updatePunishment(@NotNull Punishment punishment);

    Punishment updatePunishmentSync(@NotNull Punishment punishment);

}