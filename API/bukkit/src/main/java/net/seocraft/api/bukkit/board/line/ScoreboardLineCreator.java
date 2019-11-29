package net.seocraft.api.bukkit.board.line;

import net.seocraft.api.bukkit.board.Scoreboard;
import org.jetbrains.annotations.NotNull;

public interface ScoreboardLineCreator {

    @NotNull ScoreboardLine create(@NotNull Scoreboard scoreboard, int line, @NotNull String text);

}