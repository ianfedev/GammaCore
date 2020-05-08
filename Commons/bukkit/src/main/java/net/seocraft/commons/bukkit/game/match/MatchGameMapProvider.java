package net.seocraft.commons.bukkit.game.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.management.MapFileManager;
import net.seocraft.api.bukkit.game.map.BaseMapConfiguration;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchMapProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public class MatchGameMapProvider implements MatchMapProvider {

    @Inject private MapFileManager mapFileManager;
    @Inject private ObjectMapper mapper;

    @Override
    public @NotNull GameMap getMatchMap(@NotNull Match match) {
        Optional<GameMap> matchMap = Optional.empty();
        for (GameMap map : this.mapFileManager.getPlayableMaps().keySet()) {
            if (map.getId().equalsIgnoreCase(match.getMap())) {
                matchMap = Optional.of(map);
            }
        }
        return matchMap.orElseThrow(() -> new IllegalStateException("Core Management was processed without maps"));
    }

    @Override
    public @NotNull Location getLobbyLocation(@NotNull Match match) throws IOException {
        World matchWorld = Bukkit.getWorld("match_" + match.getId());

        if (matchWorld == null) {
            throw new IllegalStateException("Match has not been loaded");
        }

        GameMap matchMap = this.getMatchMap(match);

        BaseMapConfiguration mapConfiguration = this.mapper.readValue(
                matchMap.getConfiguration(),
                BaseMapConfiguration.class
        );

        return new Location(
                matchWorld,
                mapConfiguration.getLobbyCoordinates().getX(),
                mapConfiguration.getLobbyCoordinates().getY(),
                mapConfiguration.getLobbyCoordinates().getZ()
        );
    }

    @Override
    public @NotNull Location getSpectatorSpawnLocation(@NotNull Match match) throws IOException {
        World matchWorld = Bukkit.getWorld("match_" + match.getId());

        if (matchWorld == null) {
            throw new IllegalStateException("Match has not been loaded");
        }

        GameMap matchMap = this.getMatchMap(match);

        BaseMapConfiguration mapConfiguration = this.mapper.readValue(
                matchMap.getConfiguration(),
                BaseMapConfiguration.class
        );

        return new Location(
                matchWorld,
                mapConfiguration.getSpectatorSpawn().getX(),
                mapConfiguration.getSpectatorSpawn().getY(),
                mapConfiguration.getSpectatorSpawn().getZ()
        );
    }

}
