package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public interface CoreGameManagement {

    void initializeGameCore(@NotNull Gamemode gamemode, @NotNull SubGamemode subGamemode);

    @NotNull Gamemode getGamemode();

    @NotNull SubGamemode getSubGamemode();

    @NotNull Set<Player> getWaitingPlayers();

    void addWaitingPlayer(@NotNull Player player);

    void removeWaitingPlayer(@NotNull Player player);

    @NotNull Set<Player> getSpectatingPlayers();

    void addSpectatingPlayer(@NotNull Player player);

    void removeSpectatingPlayer(@NotNull Player player);

    void initializeMatch(@NotNull Set<Team> teams) throws IOException, Unauthorized, NotFound, BadRequest, InternalServerError;

    void finishMatch(@NotNull Match match);

    void updateMatch(@NotNull Match match) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException;

    void addMatchPlayer(@NotNull String match, @NotNull User player);

    void addSpectatorPlayer(@NotNull String match, @NotNull User player);

    void removeMatchPlayer(@NotNull String match, @NotNull User player);

    @NotNull Set<Player> getMatchPlayers(@NotNull String match);

    @NotNull Set<Player> getMatchSpectators(@NotNull String match);

    @NotNull Set<User> getMatchSpectatorsUsers(@NotNull String match);

    @NotNull Set<User> getMatchUsers(@NotNull String match);

    @Nullable Match getPlayerMatch(@NotNull Player player);

    @Nullable Match getPlayerMatch(@NotNull User user);

    @NotNull GameMap getMatchMap(@NotNull Match match);

    @NotNull Location getLobbyLocation(@NotNull Match match) throws IOException;

    @NotNull Location getSpectatorSpawnLocation(@NotNull Match match) throws IOException;

    void invalidateMatch(@NotNull Match match) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError;

}
