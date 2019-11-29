package net.seocraft.commons.bukkit.board;

import net.seocraft.api.bukkit.board.Scoreboard;
import net.seocraft.api.bukkit.board.ScoreboardRemover;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SimpleScoreboardRemover implements ScoreboardRemover {

    @Override
    public void remove(@NotNull Scoreboard scoreboard, @NotNull Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}