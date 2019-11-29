package net.seocraft.commons.bukkit.board;

import net.seocraft.api.bukkit.board.Scoreboard;
import net.seocraft.api.bukkit.board.ScoreboardApplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

public class SimpleScoreboardApplier implements ScoreboardApplier {

    @Override
    public void apply(@NotNull Scoreboard scoreboard, @NotNull Player player) {
        if (scoreboard.getViewers().size() != 1) {
            player.setScoreboard(scoreboard.getViewers().get(0).getScoreboard());
            return;
        }

        org.bukkit.scoreboard.Scoreboard bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = bukkitScoreboard.registerNewObjective(scoreboard.getTitle().length() > 16 ? scoreboard.getTitle().substring(0, 16) : scoreboard.getTitle(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(scoreboard.getTitle());

        player.setScoreboard(bukkitScoreboard);

        if (!scoreboard.getLines().isEmpty())
            scoreboard.getLines().forEach(scoreboardLine -> scoreboard.getLineCreator().create(scoreboard, scoreboardLine.getPlace(), scoreboardLine.getText()));
    }
}