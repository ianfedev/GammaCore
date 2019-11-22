package net.seocraft.commons.bukkit.game.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.event.GameInvalidationEvent;
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
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.CountdownTimer;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class CraftCoreGameManagement implements CoreGameManagement {

    @Inject private MatchProvider matchProvider;
    @Inject private MapFileManager mapFileManager;
    @Inject private ObjectMapper mapper;
    @Inject private TranslatableField translatableField;
    @Inject private ServerManager serverManager;
    @Inject private CloudManager cloudManager;
    @Inject private CommonsBukkit instance;

    private Gamemode gamemode;
    private SubGamemode subGamemode;
    private Set<Player> waitingPlayers;
    private Set<Player> spectatingPlayers;
    private Set<Match> actualMatches;
    private Multimap<String, User> matchAssignation;
    private Multimap<String, User> spectatorAssignation;
    private Map<String, Integer> remainingTime;

    @Override
    public void initializeGameCore(@NotNull Gamemode gamemode, @NotNull SubGamemode subGamemode) {
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.waitingPlayers = new HashSet<>();
        this.spectatingPlayers = new HashSet<>();
        this.actualMatches = new HashSet<>();
        this.matchAssignation = ArrayListMultimap.create();
        this.spectatorAssignation = ArrayListMultimap.create();
        this.remainingTime = new HashMap<>();
    }

    @Override
    public @NotNull Gamemode getGamemode() {
        return this.gamemode;
    }

    @Override
    public @NotNull SubGamemode getSubGamemode() {
        return this.subGamemode;
    }

    @Override
    public @NotNull Set<Player> getWaitingPlayers() {
        return this.waitingPlayers;
    }

    @Override
    public void addWaitingPlayer(@NotNull Player player) {
        this.waitingPlayers.add(player);
    }

    @Override
    public void removeWaitingPlayer(@NotNull Player player) {
        this.waitingPlayers.remove(player);
    }

    @Override
    public @NotNull Set<Player> getSpectatingPlayers() {
        return this.spectatingPlayers;
    }

    @Override
    public void addSpectatingPlayer(@NotNull Player player) {
        this.spectatingPlayers.add(player);
    }

    @Override
    public void removeSpectatingPlayer(@NotNull Player player) {
        this.spectatingPlayers.remove(player);
    }

    @Override
    public void initializeMatch() throws IOException, Unauthorized, NotFound, BadRequest, InternalServerError {

        Map<GameMap, File> playableMaps = this.mapFileManager.getPlayableMaps();

        Optional<GameMap> firstMap = playableMaps
                .keySet()
                .stream()
                .findAny();

        if (firstMap.isPresent()) {
            Match createdMatch = this.matchProvider.createMatch(
                    firstMap.get().getId(),
                    new HashSet<>(),
                    this.gamemode.getId(),
                    this.subGamemode.getId()
            );
            this.actualMatches.add(createdMatch);
            this.mapFileManager.loadMatchWorld(createdMatch);
            this.instance.getServerRecord().addMatch(createdMatch.getId());
            this.serverManager.updateServer(
                    this.instance.getServerRecord()
            );
            this.remainingTime.put(createdMatch.getId(), -1);
        } else {
            throw new InternalServerError("No playable maps could be found");
        }
    }

    @Override
    public void finishMatch(@NotNull Match match) {

        Multimap<String, User> matchAssignation = this.matchAssignation;
        Multimap<String, User> spectatorAssignation = this.spectatorAssignation;

        matchAssignation.entries().forEach((entry) -> {
            if (entry.getKey().equalsIgnoreCase(match.getId())) this.matchAssignation.remove(entry.getKey(), entry.getValue());
        });
        spectatorAssignation.entries().forEach((entry) -> {
            if (entry.getKey().equalsIgnoreCase(match.getId())) this.matchAssignation.remove(entry.getKey(), entry.getValue());
        });
        this.actualMatches.remove(match);
    }

    @Override
    public void updateMatch(@NotNull Match match) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {
        Match updatedMatch = this.matchProvider.updateMatch(match);
        this.actualMatches = this.actualMatches.stream().map(m -> {
            if (m.getId().equalsIgnoreCase(match.getId())) {
                return updatedMatch;
            } else {
                return m;
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public void addMatchPlayer(@NotNull String match, @NotNull User player) {
        this.matchAssignation.put(match, player);
    }

    @Override
    public void addSpectatorPlayer(@NotNull String match, @NotNull User player) {
        this.spectatorAssignation.put(match, player);
    }

    @Override
    public void removeMatchPlayer(@NotNull String match, @NotNull User player) {
        this.matchAssignation.entries().removeIf((entry) -> entry.getValue().getUsername().equalsIgnoreCase(player.getUsername()));
        this.spectatorAssignation.entries().removeIf((entry) -> entry.getValue().getUsername().equalsIgnoreCase(player.getUsername()));
    }

    @Override
    public Set<Match> getActualMatches() {
        return this.actualMatches;
    }

    @Override
    public @NotNull Set<Player> getMatchPlayers(@NotNull String match) {
        Set<Player> matchPlayer = new HashSet<>();
        if (!this.matchAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.matchAssignation.entries()) {
                if (entry.getKey().equalsIgnoreCase(match)) matchPlayer.add(Bukkit.getPlayer(entry.getValue().getUsername()));
            }
        }
        return matchPlayer.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public @NotNull Set<Player> getMatchSpectators(@NotNull String match) {
        Set<Player> matchPlayer = new HashSet<>();
        if (!this.spectatorAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.spectatorAssignation.entries()) {
                if (entry.getKey().equalsIgnoreCase(match)) matchPlayer.add(Bukkit.getPlayer(entry.getValue().getUsername()));
            }
        }
        return matchPlayer.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public @NotNull Set<User> getMatchSpectatorsUsers(@NotNull String match) {
        Set<User> userSet = new HashSet<>();
        if (!this.spectatorAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.spectatorAssignation.entries()) {
                userSet.add(entry.getValue());
            }
        }
        return userSet;
    }

    @Override
    public @NotNull Set<User> getMatchUsers(@NotNull String match) {
        Set<User> userSet = new HashSet<>();
        if (!this.matchAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.matchAssignation.entries()) {
                if (entry.getKey().equalsIgnoreCase(match)) userSet.add(entry.getValue());
            }
        }
        return userSet;
    }

    @Override
    public @Nullable Match getPlayerMatch(@NotNull Player player) {
        if (!this.matchAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.matchAssignation.entries()) {
                if (entry.getValue().getUsername().equalsIgnoreCase(player.getName())) {
                    for (Match match: this.actualMatches) {
                        if (match.getId().equalsIgnoreCase(entry.getKey())) return match;
                    }
                }
            }
        }
        if (!this.spectatorAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.spectatorAssignation.entries()) {
                if (entry.getValue().getUsername().equalsIgnoreCase(player.getName())) {
                    for (Match match: this.actualMatches) {
                        if (match.getId().equalsIgnoreCase(entry.getKey())) return match;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable Match getPlayerMatch(@NotNull User user) {
        if (!this.matchAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.matchAssignation.entries()) {
                if (entry.getValue().getUsername().equalsIgnoreCase(user.getUsername())) {
                    for (Match match: this.actualMatches) {
                        if (match.getId().equalsIgnoreCase(entry.getKey())) return match;
                    }
                }
            }
        }
        if (!this.spectatorAssignation.isEmpty()) {
            for (Map.Entry<String, User> entry : this.spectatorAssignation.entries()) {
                if (entry.getValue().getUsername().equalsIgnoreCase(user.getUsername())) {
                    for (Match match: this.actualMatches) {
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
            Player player = Bukkit.getPlayer(user.getUsername());
            if (player != null) {
                Bukkit.getScheduler().runTask(this.instance, () -> Bukkit.getPluginManager().callEvent(new GameSpectatorSetEvent(match, user, player, false)));
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
                () -> Bukkit.getScheduler().runTask(this.instance, () -> Bukkit.getPluginManager().callEvent(new GameInvalidationEvent(match))),
                (time) -> {
                    if (time.isImportantSecond()) {
                        this.getMatchSpectatorsUsers(match.getId()).forEach(user -> {
                            Player player = Bukkit.getPlayer(user.getUsername());
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
                        Player player = Bukkit.getPlayer(user.getUsername());
                        if (player != null) {
                            this.cloudManager.sendPlayerToGroup(player, gamemode.getLobbyGroup());
                        }
                        this.spectatingPlayers.remove(player);
                    });
                    this.finishMatch(match);
                }
        );

        timer.scheduleTimer();
    }

    @Override
    public void updateMatchRemaingTime(@NotNull String match, @NotNull Integer time) {
        this.remainingTime.put(match, time);
    }

    @Override
    public int getRemainingTime(@NotNull String match) {
        return this.remainingTime.get(match);
    }

    @Override
    public void removeMatchTime(@NotNull String match) {
        this.remainingTime.remove(match);
    }

    @Override
    public @NotNull Multimap<String, User> getMatchAssignations() {
        return this.matchAssignation;
    }

    @Override
    public @NotNull Multimap<String, User> getSpectatorAssignations() {
        return this.spectatorAssignation;
    }
}