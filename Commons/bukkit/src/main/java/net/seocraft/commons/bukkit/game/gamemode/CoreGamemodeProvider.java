package net.seocraft.commons.bukkit.game.gamemode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeCache;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.backend.gamemode.GamemodeGetRequest;
import net.seocraft.commons.core.backend.gamemode.GamemodeListRequest;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

public class CoreGamemodeProvider implements GamemodeProvider {

    @Inject private ListeningExecutorService executorService;
    @Inject private GamemodeGetRequest gamemodeGetRequest;
    @Inject private RedisClient redisClient;
    @Inject private ObjectMapper mapper;
    @Inject private GamemodeListRequest gamemodeListRequest;
    @Inject private GamemodeCache gamemodeCache;
    @Inject private CommonsBukkit instance;

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Gamemode>> getCachedGamemode(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getCachedGamemodeSync(id));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull Gamemode getCachedGamemodeSync(@NotNull String id) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError {
        if (this.redisClient.existsKey("gamemode:" + id)) {
            return this.mapper.readValue(this.redisClient.getString("gamemode:" + id), Gamemode.class);
        } else {
            return findGamemodeByIdSync(id);
        }
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Gamemode>> findGamemodeById(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, findGamemodeByIdSync(id));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull Gamemode findGamemodeByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        String gamemodeResponse = this.gamemodeGetRequest.executeRequest(
                id
        );
        Gamemode gamemode = this.mapper.readValue(gamemodeResponse, Gamemode.class);
        this.gamemodeCache.cacheMatch(gamemode);
        return gamemode;
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Set<Gamemode>>> listGamemodes() {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, listGamemodesSync() );
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull Set<Gamemode> listGamemodesSync() throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        String gamemodeResponse = this.gamemodeListRequest.executeRequest();
        Set<Gamemode> gamemodeSet = this.mapper.readValue(gamemodeResponse, new TypeReference<Set<Gamemode>>(){});
        gamemodeSet.forEach(gamemode -> {
            try {
                this.gamemodeCache.cacheMatch(gamemode);
            } catch (JsonProcessingException e) {
                Bukkit.getLogger().log(Level.WARNING, "There was an error retreiving a gamemode from gamemode list", e);
            }
        });
        return gamemodeSet;
    }

    @Override
    public @Nullable Gamemode getServerGamemode() throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {
        if (this.instance.getServerRecord().getGamemode() != null) {
            return this.getCachedGamemodeSync(this.instance.getServerRecord().getGamemode());
        }
        return null;
    }

    @Override
    public @Nullable SubGamemode getServerSubgamemode() throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {
        if (this.instance.getServerRecord().getGamemode() != null &&  this.instance.getServerRecord().getSubGamemode() != null) {
            Gamemode gamemode = this.getCachedGamemodeSync(this.instance.getServerRecord().getGamemode());
            for (SubGamemode subGamemode : gamemode.getSubGamemodes()) {
                if (subGamemode.getId().equalsIgnoreCase(this.instance.getServerRecord().getSubGamemode())) return subGamemode;
            }
        }
        return null;
    }

}
