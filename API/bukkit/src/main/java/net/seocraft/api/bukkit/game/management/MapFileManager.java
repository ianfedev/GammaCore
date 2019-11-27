package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.match.Match;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface MapFileManager {

    void configureMapFolder();

    @NotNull Map<GameMap, File> getPlayableMaps();

    @NotNull World loadMatchWorld(@NotNull Match match) throws IOException;

    void unloadMatchWorld(@NotNull Match match) throws IOException;

}