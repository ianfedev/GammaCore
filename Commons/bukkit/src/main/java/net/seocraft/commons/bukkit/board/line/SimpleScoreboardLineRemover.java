package net.seocraft.commons.bukkit.board.line;

import net.seocraft.api.bukkit.board.Scoreboard;
import net.seocraft.api.bukkit.board.line.ScoreboardLine;
import net.seocraft.api.bukkit.board.line.ScoreboardLineRemover;
import org.jetbrains.annotations.NotNull;

public class SimpleScoreboardLineRemover implements ScoreboardLineRemover {

    @Override
    public void remove(@NotNull Scoreboard scoreboard, @NotNull ScoreboardLine scoreboardLine) {
        if (scoreboard.getViewers().isEmpty()) return;

        org.bukkit.scoreboard.Scoreboard bukkitScoreboard = scoreboard.getViewers().get(0).getScoreboard();
        bukkitScoreboard.resetScores(scoreboardLine.getEntry());
    }
}