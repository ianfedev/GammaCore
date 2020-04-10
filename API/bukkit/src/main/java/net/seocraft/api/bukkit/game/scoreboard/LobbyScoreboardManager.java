package net.seocraft.api.bukkit.game.scoreboard;

import net.seocraft.api.bukkit.game.match.Match;
import org.jetbrains.annotations.NotNull;

public interface LobbyScoreboardManager {

    void setLobbyScoreboard(@NotNull Match match);

    void clearScoreboard(@NotNull String userName);

    void setScoreboardTask(@NotNull String userName, int task);

}
