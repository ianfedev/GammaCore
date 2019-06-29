package net.seocraft.api.bukkit.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GamemodeHandler {

    //TODO: Create Implementation

    /**
     * Always use with one @NotNull parameter.
     *
     * @param id Database ID.
     * @param name Database name.
     * @return Gamemode model.
     */
    @Nullable Gamemode getGamemode(@Nullable String id, @Nullable String name);


    /**
     * @return Unordered list of gamemodes.
     */
    @NotNull List<Gamemode> listGamemodes();
}
