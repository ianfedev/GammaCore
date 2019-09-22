package net.seocraft.commons.bukkit.game.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.MapFileManager;
import net.seocraft.api.bukkit.game.management.GameLoginManager;
import net.seocraft.api.bukkit.game.map.BaseMapConfiguration;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

public class CraftGameLoginManager implements GameLoginManager {

    @Inject private MapFileManager mapFileManager;
    @Inject private ObjectMapper mapper;
    @Inject private CoreGameManagement coreGameManagement;
    @Inject private TranslatableField translatableField;

    @Override
    public void matchPlayerJoin(@NotNull FinderResult match, @NotNull User user, @NotNull Player player) {
        if (match.isSpectable()) {
            // TODO: Set spectator tools
        } else {

            Optional<GameMap> matchMap = this.mapFileManager.getPlayableMaps()
                    .keySet()
                    .stream()
                    .filter(map -> map.getId().equalsIgnoreCase(match.getMatch().getId()))
                    .findAny();

            if (matchMap.isPresent()) {
                try {
                    BaseMapConfiguration mapConfiguration = this.mapper.readValue(
                            matchMap.get().getConfiguration(),
                            BaseMapConfiguration.class
                    );

                    player.setHealth(20);
                    player.setFoodLevel(20);

                    World matchWorld = Bukkit.getWorld("match_" + match.getMatch().getId());
                    if (matchWorld != null) {
                        Location location = new Location(
                                matchWorld,
                                mapConfiguration.getLobbyCoordinates().getX(),
                                mapConfiguration.getLobbyCoordinates().getY(),
                                mapConfiguration.getLobbyCoordinates().getZ()
                        );

                        player.teleport(location);


                    } else {
                        player.kickPlayer(ChatColor.RED +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_pairing_error"));
                    }
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "[Game API] There was an error paring user {0} to a match. ({1})",
                            new Object[]{user.getUsername(), e.getMessage()});
                    player.kickPlayer(ChatColor.RED +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_pairing_error"));
                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, "[Game API] There was an error paring user {0} to a match.",
                       user.getUsername());
                player.kickPlayer(ChatColor.RED +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_pairing_error"));
            }
        }
    }
}
