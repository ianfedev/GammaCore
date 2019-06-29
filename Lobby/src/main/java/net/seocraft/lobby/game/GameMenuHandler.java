package net.seocraft.lobby.game;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface GameMenuHandler {

    void loadGameMenu(@NotNull Player player, @NotNull String l);

}