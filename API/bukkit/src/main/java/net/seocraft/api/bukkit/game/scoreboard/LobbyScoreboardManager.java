package net.seocraft.api.bukkit.game.scoreboard;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface LobbyScoreboardManager {

    void retrieveGameBoard(@NotNull Match match, @NotNull Player player, @NotNull User user);

    void updateBoardCountDown(@NotNull Match match, int count);


}
