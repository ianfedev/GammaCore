package net.seocraft.api.bukkit.game.gamemode;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GamemodeProvider {

    /**
     * Get AsyncResponse wrapped gamemode.
     * @see AsyncResponse
     * @param id Database ID.
     * @return ListenableFuture with AsyncResponse wrapped Gamemode.
     */

    @NotNull ListenableFuture<AsyncResponse<Gamemode>> getGamemode(@NotNull String id);

    /**
     * Obtain gamemode registered in database
     * @param id Database ID.
     * @return Gamemode model.
     */
    @Nullable Gamemode getGamemodeSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    /**
     * @return ListenableFuture with AsyncResponse wrapper gamemode list.
     */
    @NotNull ListenableFuture<AsyncResponse<List<Gamemode>>> listGamemodes();

    /**
     * @see Gamemode
     * @return Unordered list of gamemodes.
     */
    @Nullable List<Gamemode> listGamemodesSync() throws Unauthorized, BadRequest, NotFound, InternalServerError;
}
