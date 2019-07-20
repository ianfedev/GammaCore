package net.seocraft.commons.bukkit.old.punishment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.commons.bukkit.server.BukkitTokenQuery;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.punishment.*;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@Singleton
public class IPunishmentHandler implements PunishmentHandler {

    private Gson gson;
    private ListeningExecutorService executorService;
    private Channel<Punishment> punishmentChannel;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private PunishmentCreateRequest punishmentCreateRequest;
    @Inject private PunishmentActions punishmentActions;
    @Inject private PunishmentGetRequest punishmentGetRequest;
    @Inject private PunishmentGetLastRequest punishmentGetLastRequest;
    @Inject private PunishmentListRequest punishmentListRequest;
    @Inject private PunishmentUpdateRequest punishmentUpdateRequest;
    @Inject private BukkitTokenQuery serverTokenQuery;

    @Inject IPunishmentHandler(ListeningExecutorService executorService, Messager messager, Gson gson) {
        this.executorService = executorService;
        this.gson = gson;
        this.punishmentChannel = messager.getChannel("punishments", Punishment.class);
        this.punishmentChannel.registerListener(new PunishmentListener(this.userStorageProvider, this.punishmentActions));
    }

    @Override
    public @NotNull Punishment createPunishment(@NotNull PunishmentType punishmentType, @NotNull String punisher, @NotNull String punished, @NotNull String server, @Nullable Match match, @NotNull String lastIp, @NotNull String reason, long expiration, boolean automatic, boolean silent) throws Unauthorized, BadRequest, NotFound, InternalServerError {

        if (punishmentType.equals(PunishmentType.BAN)) {
            Punishment previousPunishment = getLastPunishmentSync(punishmentType, punished);

            if (previousPunishment != null) {
                previousPunishment.setActive(false);
                updatePunishmentSync(previousPunishment);
            }
        }

        Punishment punishment = new IPunishment(UUID.randomUUID().toString(), punishmentType, punisher, punished, server, match, lastIp, reason, expiration, 0, automatic, false, silent);
        this.punishmentCreateRequest.executeRequest(
                this.gson.toJson(
                        punishment,
                        IPunishment.class
                ),
                this.serverTokenQuery.getToken()
        );
        this.punishmentChannel.sendMessage(punishment);
        return punishment;
    }

    @Override
    public ListenableFuture<AsyncResponse<Punishment>> getPunishmentById(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getPunishmentByIdSync(id));
            } catch (Unauthorized | InternalServerError | NotFound exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @Nullable Punishment getPunishmentByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        return this.gson.fromJson(
                this.punishmentGetRequest.executeRequest(id, this.serverTokenQuery.getToken()),
                IPunishment.class
        );
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Punishment>> getLastPunishment(@Nullable PunishmentType type, @NotNull String playerId) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getLastPunishmentSync(type, playerId));
            } catch (Unauthorized | InternalServerError | NotFound exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @Nullable Punishment getLastPunishmentSync(@Nullable PunishmentType type, @NotNull String playerId) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String typeString = null;
        if (type != null) typeString = type.toString();
        return this.gson.fromJson(
                this.punishmentGetLastRequest.executeRequest(typeString, playerId, this.serverTokenQuery.getToken()),
                IPunishment.class
        );
    }


    @Override
    public @NotNull ListenableFuture<AsyncResponse<List<Punishment>>> getPunishments(@Nullable PunishmentType type, @Nullable String playerId, boolean active) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getPunishmentsSync(type, playerId, active));
            } catch (Unauthorized | InternalServerError | NotFound exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public List<Punishment> getPunishmentsSync(@Nullable PunishmentType type, @Nullable String playerId, boolean active) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String typeString = null;
        if (type != null) typeString = type.toString();
        return this.gson.fromJson(
                this.punishmentListRequest.executeRequest(typeString, playerId, active, this.serverTokenQuery.getToken()),
                new TypeToken<List<IPunishment>>(){}.getType()
        );
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Punishment>> updatePunishment(@NotNull Punishment punishment) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, updatePunishmentSync(punishment));
            } catch (Unauthorized | InternalServerError | NotFound exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull Punishment updatePunishmentSync(@NotNull Punishment punishment) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        return this.gson.fromJson(
                this.punishmentUpdateRequest.executeRequest(
                        this.gson.toJson(punishment, IPunishment.class),
                        punishment.id(),
                        this.serverTokenQuery.getToken()
                ),
                IPunishment.class
        );
    }
}
