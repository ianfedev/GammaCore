package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.map.GameMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface CoreGameManagement {

    void initializeGameCore(@NotNull Gamemode gamemode, @NotNull SubGamemode subGamemode);

    @NotNull Gamemode getGamemode();

    @NotNull SubGamemode getSubGamemode();

    @NotNull Set<Player> getWaitingPlayers();

    void addWaitingPlayer(Player player);

    void removeWaitingPlayer(Player player);

    @NotNull Set<Player> getSpectatingPlayers();

    void addSpectatingPlayer(Player player);

    void removeSpectatingPlayer(Player player);

}
