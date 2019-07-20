package net.seocraft.commons.bukkit.game.gamemode;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.commons.core.backend.gamemode.GamemodeGetRequest;
import net.seocraft.commons.core.backend.gamemode.GamemodeListRequest;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoreGamemodeProvider implements GamemodeProvider {

    @Inject private ListeningExecutorService executorService;
    @Inject private GamemodeGetRequest gamemodeGetRequest;
    @Inject private Gson gson;
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
    public @Nullable Gamemode getGamemodeSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String gamemodeResponse = this.gamemodeGetRequest.executeRequest(
                id
        );
        return this.gson.fromJson(gamemodeResponse, Gamemode.class);
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<List<Gamemode>>> listGamemodes() {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, listGamemodesSync() );
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull List<Gamemode> listGamemodesSync() throws Unauthorized, BadRequest, NotFound, InternalServerError {
        String gamemodeResponse = this.gamemodeListRequest.executeRequest();
        return this.gson.fromJson(gamemodeResponse, new TypeToken<List<CoreGamemode>>(){}.getType());
    }

}
