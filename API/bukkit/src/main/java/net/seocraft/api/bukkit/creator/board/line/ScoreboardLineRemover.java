package net.seocraft.api.bukkit.creator.board.line;

import net.seocraft.api.bukkit.creator.board.Scoreboard;
import org.jetbrains.annotations.NotNull;

public interface ScoreboardLineRemover {

    void remove(@NotNull Scoreboard scoreboard, @NotNull ScoreboardLine scoreboardLine);

}