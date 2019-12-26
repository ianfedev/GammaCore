package net.seocraft.commons.bukkit.creator.board.line;

import net.seocraft.api.bukkit.creator.board.Scoreboard;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLine;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineCreator;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SimpleScoreboardLineCreator implements ScoreboardLineCreator {

    @Override
    public @NotNull
    ScoreboardLine create(@NotNull Scoreboard scoreboard, int line, @NotNull String text) {
        ScoreboardLine scoreboardLine = new CraftScoreboardLine(line, text, text);
        if (scoreboard.getViewers().isEmpty()) return scoreboardLine;

        org.bukkit.scoreboard.Scoreboard bukkitScoreboard = scoreboard.getViewers().get(0).getScoreboard();
        Objective objective = bukkitScoreboard.getObjective(scoreboard.getTitle().length() > 16 ? scoreboard.getTitle().substring(0, 16) : scoreboard.getTitle());

        Team team = Optional.ofNullable(bukkitScoreboard.getTeam("line" + line)).orElseGet(() -> bukkitScoreboard.registerNewTeam("line" + line));
        team.addEntry(scoreboardLine.getEntry());
        objective.getScore(scoreboardLine.getEntry()).setScore(line);

        return scoreboardLine;
    }
}