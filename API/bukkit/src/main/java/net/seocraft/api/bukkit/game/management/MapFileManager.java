package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.map.GameMap;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface MapFileManager {

    void configureMapFolder();

    @NotNull Set<GameMap> getPlayableMaps();

}