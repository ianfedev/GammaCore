package net.seocraft.commons.bukkit.punishment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.bukkit.punishment.PunishmentProvider;
import net.seocraft.api.bukkit.punishment.PunishmentType;
import net.seocraft.api.core.user.UserExpulsion;
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

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Singleton
public class UserPunishmentProvider implements PunishmentProvider {

    private ListeningExecutorService executorService;
    private Channel<Punishment> punishmentChannel;
    private Channel<UserExpulsion> expulsionChannel;
    @Inject private ObjectMapper mapper;
    @Inject private PunishmentCreateRequest punishmentCreateRequest;
    @Inject private PunishmentGetRequest punishmentGetRequest;
    @Inject private PunishmentGetLastRequest punishmentGetLastRequest;
    @Inject private PunishmentListRequest punishmentListRequest;
    @Inject private PunishmentUpdateRequest punishmentUpdateRequest;
    @Inject private BukkitTokenQuery serverTokenQuery;

    @Inject UserPunishmentProvider(UserStorageProvider userStorageProvider, PunishmentActions punishmentActions, ListeningExecutorService executorService, Messager messager) {
        this.executorService = executorService;
        this.punishmentChannel = messager.getChannel("punishments", Punishment.class);
        this.expulsionChannel = messager.getChannel("proxyBan", UserExpulsion.class);
        this.punishmentChannel.registerListener(new PunishmentListener(userStorageProvider, punishmentActions, expulsionChannel));
    }

    @Override
    public @NotNull Punishment createPunishment(@NotNull PunishmentType punishmentType, @NotNull String punisher, @NotNull String punished, @NotNull String server, @Nullable Match match, @NotNull String lastIp, @NotNull String reason, long expiration, boolean automatic, boolean silent) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {

        if (punishmentType.equals(PunishmentType.BAN)) {
            Punishment previousPunishment = getLastPunishmentSync(punishmentType, punished);

            if (previousPunishment != null) {
                previousPunishment.setActive(false);
                updatePunishmentSync(previousPunishment);
            }
        }

        Punishment punishment = new UserPunishment(UUID.randomUUID().toString(), punishmentType, punisher, punished, server, match, lastIp, reason, expiration, 0, automatic, false, silent, true);
        this.punishmentCreateRequest.executeRequest(
                this.mapper.writeValueAsString(
                        punishment
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
    public @Nullable Punishment getPunishmentByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        return this.mapper.readValue(
                this.punishmentGetRequest.executeRequest(id, this.serverTokenQuery.getToken()),
                Punishment.class
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
    public @Nullable Punishment getLastPunishmentSync(@Nullable PunishmentType type, @NotNull String playerId) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        String typeString = "warn";
        if (type != null) typeString = type.toString();

        String response = this.punishmentGetLastRequest.executeRequest(typeString, playerId, this.serverTokenQuery.getToken());
        if (response.equals("")) return null;
        return this.mapper.readValue(
                response,
                Punishment.class
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
    public List<Punishment> getPunishmentsSync(@Nullable PunishmentType type, @Nullable String playerId, boolean active) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        String typeString = null;
        if (type != null) typeString = type.toString();
        return this.mapper.readValue(
                this.punishmentListRequest.executeRequest(typeString, playerId, active, this.serverTokenQuery.getToken()),
                new TypeReference<Set<Punishment>>(){}
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
    public @NotNull Punishment updatePunishmentSync(@NotNull Punishment punishment) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        return this.mapper.readValue(
                this.punishmentUpdateRequest.executeRequest(
                        this.mapper.writeValueAsString(punishment),
                        punishment.getId(),
                        this.serverTokenQuery.getToken()
                ),
                Punishment.class
        );
    }
}
