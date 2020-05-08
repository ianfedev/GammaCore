package net.seocraft.api.bukkit.game.scoreboard;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface LobbyScoreboardManager {

    void retrieveGameBoard(@NotNull Match match, @NotNull Player player, @NotNull User user) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError;

    void updateBoardCountDown(@NotNull Match match, int count);


}
