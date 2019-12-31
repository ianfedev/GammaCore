package net.seocraft.commons.bukkit.game.gamemode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.gamemode.GamemodeGetRequest;
import net.seocraft.commons.core.backend.gamemode.GamemodeListRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public class CoreGamemodeProvider implements GamemodeProvider {

    @Inject private ListeningExecutorService executorService;
    @Inject private GamemodeGetRequest gamemodeGetRequest;
    @Inject private ObjectMapper mapper;
    @Inject private GamemodeListRequest gamemodeListRequest;

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Gamemode>> getGamemode(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getGamemodeSync(id));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @Nullable Gamemode getGamemodeSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        String gamemodeResponse = this.gamemodeGetRequest.executeRequest(
                id
        );
        return this.mapper.readValue(gamemodeResponse, CoreGamemode.class);
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
        return this.mapper.readValue(gamemodeResponse, new TypeReference<Set<Gamemode>>(){});
    }

}
