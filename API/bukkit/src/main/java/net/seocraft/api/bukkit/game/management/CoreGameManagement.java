package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    void initializeMatch(@NotNull Match match);

    void updateMatch(@NotNull Match match) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException;

    void addMatchPlayer(@NotNull String match, @NotNull User player);

    void removeMatchPlayer(@NotNull String match, @NotNull User player);

    @NotNull Set<Player> getMatchPlayers(@NotNull String match);

    @NotNull Set<User> getMatchUsers(@NotNull String match);

}
