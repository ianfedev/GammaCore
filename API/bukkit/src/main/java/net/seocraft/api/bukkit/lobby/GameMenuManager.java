package net.seocraft.api.bukkit.lobby;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface GameMenuManager {

    void loadGameMenu(@NotNull Player player, @NotNull String l);

}