package net.seocraft.commons.bukkit.game.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.event.GameSpectatorSetEvent;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.MapFileManager;
import net.seocraft.api.bukkit.game.map.BaseMapConfiguration;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.bukkit.util.CountdownTimer;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class CraftCoreGameManagement implements CoreGameManagement {

    @Inject private MatchProvider matchProvider;
    @Inject private MapFileManager mapFileManager;
    @Inject private ObjectMapper mapper;
    @Inject private TranslatableField translatableField;
    @Inject private CloudManager cloudManager;
    @Inject private CommonsBukkit instance;



    @Override
    public @NotNull Gamemode getGamemode() {
        return this.instance.serverGamemode;
    }

    @Override
    public @NotNull SubGamemode getSubGamemode() {
        return this.instance.serverSubGamemode;
    }

    @Override
    public @NotNull Set<Player> getWaitingPlayers() {
        return this.instance.waitingPlayers;
    }

    @Override
    public void addWaitingPlayer(@NotNull Player player) {
        this.instance.waitingPlayers.add(player);
    }

    @Override
    public void removeWaitingPlayer(@NotNull Player player) {
        this.instance.waitingPlayers.remove(player);
    }

    @Override
    public @NotNull Set<Player> getSpectatingPlayers() {
        return this.instance.spectatingPlayers;
    }

    @Override
    public void addSpectatingPlayer(@NotNull Player player) {
        this.instance.spectatingPlayers.add(player);
    }

    @Override
    public void removeSpectatingPlayer(@NotNull Player player) {
        this.instance.spectatingPlayers.remove(player);
    }

    @Override
    public void initializeMatch(@NotNull Match match) {
        this.instance.actualMatches.add(match);
    }

    @Override
    public void finishMatch(@NotNull Match match) {
        Map<String, User> matchAssignation = this.instance.matchAssignation;
        Map<String, User> spectatorAssignation = this.instance.spectatorAssignation;

        matchAssignation.forEach((matchId, user) -> {
            if (matchId.equalsIgnoreCase(match.getId())) this.instance.matchAssignation.remove(matchId);
        });
        spectatorAssignation.forEach((matchId, user) -> {
            if (matchId.equalsIgnoreCase(match.getId())) this.instance.spectatorAssignation.remove(matchId);
        });
        this.instance.actualMatches.remove(match);
    }

    @Override
    public void updateMatch(@NotNull Match match) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {

        Match updatedMatch = this.matchProvider.updateMatch(match);
        Map<String, User> matchAssignation = this.instance.matchAssignation;

        matchAssignation.forEach((processMatch, list) -> {
            if (processMatch.equalsIgnoreCase(match.getId())) {
                this.instance.actualMatches.remove(match);
                this.instance.actualMatches.add(updatedMatch);
            }
        });
    }

    @Override
    public void addMatchPlayer(@NotNull String match, @NotNull User player) {
        this.instance.matchAssignation.put(match, player);
    }

    @Override
    public void addSpectatorPlayer(@NotNull String match, @NotNull User player) {
        this.instance.spectatorAssignation.put(match, player);
    }

    @Override
    public void removeMatchPlayer(@NotNull String match, @NotNull User player) {
        Map<String, User> matchAssignation = this.instance.matchAssignation;
        Map<String, User> spectatorAssignation = this.instance.spectatorAssignation;

        if (!this.instance.matchAssignation.isEmpty()) {
            matchAssignation.forEach((matchId, user) -> {
                if (user.getUsername().equalsIgnoreCase(player.getUsername())) {
                    this.instance.matchAssignation.remove(matchId, user);
                }
            });
        }
        if (!this.instance.spectatorAssignation.isEmpty()){
            spectatorAssignation.forEach((matchId, user) -> {
                if (user.getUsername().equalsIgnoreCase(player.getUsername())) {
                    this.instance.spectatorAssignation.remove(matchId, user);
                }
            });
        }
    }

    @Override
    public @NotNull Set<Player> getMatchPlayers(@NotNull String match) {
        System.out.println("Match players: " + match);
        Set<Player> matchPlayer = new HashSet<>();
        if (!this.instance.matchAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.instance.matchAssignation.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue().getUsername());
                if (entry.getKey().equalsIgnoreCase(match)) matchPlayer.add(Bukkit.getPlayer(entry.getValue().getUsername()));
            }
        }
        return matchPlayer.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public @NotNull Set<Player> getMatchSpectators(@NotNull String match) {
        Set<Player> matchPlayer = new HashSet<>();
        if (!this.instance.spectatorAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.instance.spectatorAssignation.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(match)) matchPlayer.add(Bukkit.getPlayer(entry.getValue().getUsername()));
            }
        }
        return matchPlayer.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public @NotNull Set<User> getMatchSpectatorsUsers(@NotNull String match) {
        Set<User> userSet = new HashSet<>();
        if (!this.instance.spectatorAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.instance.spectatorAssignation.entrySet()) {
                userSet.add(entry.getValue());
            }
        }
        return userSet;
    }

    @Override
    public @NotNull Set<User> getMatchUsers(@NotNull String match) {
        Set<User> userSet = new HashSet<>();
        if (!this.instance.matchAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.instance.matchAssignation.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(match)) userSet.add(entry.getValue());
            }
        }
        return userSet;
    }

    @Override
    public @Nullable Match getPlayerMatch(@NotNull Player player) {
        if (!this.instance.matchAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.instance.matchAssignation.entrySet()) {
                if (entry.getValue().getUsername().equalsIgnoreCase(player.getName())) {
                    for (Match match: this.instance.actualMatches) {
                        if (match.getId().equalsIgnoreCase(entry.getKey())) return match;
                    }
                }
            }
        }
        if (!this.instance.spectatorAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.instance.spectatorAssignation.entrySet()) {
                if (entry.getValue().getUsername().equalsIgnoreCase(player.getName())) {
                    for (Match match: this.instance.actualMatches) {
                        if (match.getId().equalsIgnoreCase(entry.getKey())) return match;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable Match getPlayerMatch(@NotNull User user) {
        if (!this.instance.matchAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.instance.matchAssignation.entrySet()) {
                if (entry.getValue().getUsername().equalsIgnoreCase(user.getUsername())) {
                    for (Match match: this.instance.actualMatches) {
                        if (match.getId().equalsIgnoreCase(entry.getKey())) return match;
                    }
                }
            }
        }
        if (!this.instance.spectatorAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.instance.spectatorAssignation.entrySet()) {
                if (entry.getValue().getUsername().equalsIgnoreCase(user.getUsername())) {
                    for (Match match: this.instance.actualMatches) {
                        if (match.getId().equalsIgnoreCase(entry.getKey())) return match;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull GameMap getMatchMap(@NotNull Match match) {
        Optional<GameMap> matchMap = this.mapFileManager.getPlayableMaps()
                .keySet()
                .stream()
                .filter(map -> map.getId().equalsIgnoreCase(match.getMap()))
                .findFirst();
        if (!matchMap.isPresent()) throw new IllegalStateException("Core Management was processed without maps");
        return matchMap.get();
    }

    @Override
    public @NotNull Location getLobbyLocation(@NotNull Match match) throws IOException {
        World matchWorld = Bukkit.getWorld("match_" + match.getId());
        if (matchWorld != null) {
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
        } else {
            throw new IllegalStateException("Match has not been loaded");
        }
    }

    @Override
    public @NotNull Location getSpectatorSpawnLocation(@NotNull Match match) throws IOException {
        World matchWorld = Bukkit.getWorld("match_" + match.getId());
        if (matchWorld != null) {
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
        } else {
            throw new IllegalStateException("Match has not been loaded");
        }
    }

    @Override
    public void invalidateMatch(@NotNull Match match) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError {
        match.setStatus(MatchStatus.INVALIDATED);
        this.updateMatch(match);
        this.getMatchUsers(match.getId()).forEach(user -> {
            Player player = Bukkit.getPlayer(user.getId());
            if (player != null) {
                Bukkit.getPluginManager().callEvent(new GameSpectatorSetEvent(match, user, player, false, false));
                ChatAlertLibrary.infoAlert(
                        player,
                        this.translatableField.getUnspacedField(
                                user.getLanguage(),
                                "commons_invalidation_success"
                        ) + "."
                );
            }
        });

        CountdownTimer timer = new CountdownTimer(
                this.instance,
                120,
                (time) -> {
                    if (time.isImportantSecond()) {
                        this.getMatchSpectatorsUsers(match.getId()).forEach(user -> {
                            Player player = Bukkit.getPlayer(user.getId());
                            if (player != null) {
                                ChatAlertLibrary.infoAlert(
                                        player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_invalidation_expulse"
                                        ).replace(
                                                "%%seconds%%",
                                                ChatColor.RED + "" + time.getSecondsLeft() + " " + ChatColor.AQUA
                                        )
                                );
                            }
                        });
                    }
                },
                () -> {
                    this.getMatchSpectatorsUsers(match.getId()).forEach(user -> {
                        Player player = Bukkit.getPlayer(user.getId());
                        if (player != null) {
                            this.cloudManager.sendPlayerToGroup(player, this.getGamemode().getLobbyGroup());
                        }
                        this.instance.spectatingPlayers.remove(player);
                    });
                    this.finishMatch(match);
                }
        );

        timer.scheduleTimer();
    }
}