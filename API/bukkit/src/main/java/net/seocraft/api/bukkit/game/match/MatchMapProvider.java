package net.seocraft.api.bukkit.game.match;

import net.seocraft.api.bukkit.game.map.GameMap;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface MatchMapProvider {

    @NotNull GameMap getMatchMap(@NotNull Match match);

    @NotNull Location getLobbyLocation(@NotNull Match match) throws IOException;

    @NotNull Location getSpectatorSpawnLocation(@NotNull Match match) throws IOException;

}
