package net.seocraft.api.bukkit.game;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.shared.gamemode.GamemodeGetRequest;
import net.seocraft.api.shared.gamemode.GamemodeListRequest;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GamemodeHandlerImp implements GamemodeHandler {

    @Inject private ListeningExecutorService executorService;
    @Inject private GamemodeGetRequest gamemodeGetRequest;
    @Inject private Gson gson;
    @Inject private ServerTokenQuery serverTokenQuery;
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
                id,
                this.serverTokenQuery.getToken()
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
        String gamemodeResponse = this.gamemodeListRequest.executeRequest(
                this.serverTokenQuery.getToken()
        );
        return this.gson.fromJson(gamemodeResponse, new TypeToken<List<GamemodeImp>>(){}.getType());
    }

}
