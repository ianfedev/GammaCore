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

    void addWaitingPlayer(Player player);

    void removeWaitingPlayer(Player player);

    @NotNull Set<Player> getSpectatingPlayers();

    void addSpectatingPlayer(Player player);

    void removeSpectatingPlayer(Player player);

    void initializeMatch(Match match);

    void updateMatch(Match match) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException;

    void addMatchPlayer(String match, User player);

    void removeMatchPlayer(String match, User player);

}
