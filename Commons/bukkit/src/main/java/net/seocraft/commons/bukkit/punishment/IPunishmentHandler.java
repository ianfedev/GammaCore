package net.seocraft.commons.bukkit.punishment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.Match;
import net.seocraft.api.shared.punishment.PunishmentCreateRequest;
import net.seocraft.api.shared.punishment.PunishmentGetRequest;
import net.seocraft.api.shared.redis.Channel;
import net.seocraft.api.shared.redis.Messager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class IPunishmentHandler implements PunishmentHandler {

    private Gson gson;
    private ListeningExecutorService executorService;
    private Messager messager;
    private Channel<Punishment> punishmentChannel;
    private String serverToken;
    @Inject private PunishmentCreateRequest punishmentCreateRequest;
    @Inject private PunishmentGetRequest punishmentGetRequest;
    @Inject private ServerTokenQuery serverTokenQuery;

    @Inject IPunishmentHandler(ListeningExecutorService executorService, Messager messager, Gson gson, ServerTokenQuery serverTokenQuery) {
        this.executorService = executorService;
        this.messager = messager;
        this.gson = gson;
        this.punishmentChannel = this.messager.getChannel("punishments", Punishment.class);
        this.serverTokenQuery = serverTokenQuery;
        this.serverToken = this.serverTokenQuery.getToken();
        // TODO: Create punishment event / listener registration
    }

    @Override
    public @NotNull Punishment createPunishment(@NotNull PunishmentType punishmentType, @NotNull String punisher, @NotNull String punished, @NotNull String server, @Nullable Match match, @NotNull String lastIp, @NotNull String reason, @NotNull String evidence, long expiration, boolean automatic, boolean silent) throws Unauthorized, BadRequest, NotFound, InternalServerError {

        if (punishmentType.equals(PunishmentType.BAN)) {
            Punishment previousPunishment = getLastPunishmentSync(punishmentType, punished);
            if (previousPunishment != null) {
                previousPunishment.setActive(false);
                updatePunishmentSync(previousPunishment);
            }
        }

        Punishment punishment = new IPunishment(UUID.randomUUID().toString(), punishmentType, punisher, punished, server, match, lastIp, reason, expiration, 0, automatic, false, silent);

        //TODO: Execute request
        this.punishmentCreateRequest.executeRequest(
                this.gson.toJson(
                        punishment,
                        Punishment.class
                ),
                this.serverTokenQuery.getToken()
        );
        return punishment;
    }

    @Override
    public ListenableFuture<AsyncResponse<Punishment>> getPunishmentById(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return AsyncResponse.getSucessResponse(getPunishmentByIdSync(id));
            } catch (Unauthorized | InternalServerError | NotFound exception) {
                return new AsyncResponse<Punishment>(exception, AsyncResponse.Status.ERROR,null);
            }
        });
    }

    @Override
    public @Nullable Punishment getPunishmentByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        return this.gson.fromJson(
                this.punishmentGetRequest.executeRequest(id, this.serverToken),
                Punishment.class
        );
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Punishment>> getLastPunishment(@NotNull PunishmentType type, @NotNull String playerId) {
        return null;
    }

    @Override
    public @Nullable Punishment getLastPunishmentSync(@NotNull PunishmentType type, @NotNull String playerId) {
        return null;
    }

    @Override
    public ListenableFuture<AsyncResponse<List<Punishment>>> getPunishments(@Nullable PunishmentType type, @Nullable String playerId, boolean active) {
        return null;
    }

    @Override
    public List<Punishment> getPunishmentsSync(@Nullable PunishmentType type, @Nullable String playerId, boolean active) {
        return null;
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Punishment>> updatePunishment(@NotNull Punishment punishment) {
        return null;
    }

    @Override
    public Punishment updatePunishmentSync(@NotNull Punishment punishment) {
        return null;
    }
}
