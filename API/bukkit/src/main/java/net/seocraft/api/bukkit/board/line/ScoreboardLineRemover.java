package net.seocraft.api.bukkit.board.line;

import net.seocraft.api.bukkit.board.Scoreboard;
import org.jetbrains.annotations.NotNull;

public interface ScoreboardLineRemover {

    void remove(@NotNull Scoreboard scoreboard, @NotNull ScoreboardLine scoreboardLine);

}