package net.seocraft.api.bukkit.creator.board;

import net.seocraft.api.bukkit.creator.board.line.ScoreboardLine;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineCreator;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineRemover;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface Scoreboard {

    @NotNull String getTitle();

    @NotNull List<Player> getViewers();

    @NotNull List<ScoreboardLine> getLines();

    @NotNull ScoreboardApplier getApplier();

    @NotNull ScoreboardRemover getRemover();

    @NotNull ScoreboardLineCreator getLineCreator();

    @NotNull ScoreboardLineRemover getLineRemover();

    @NotNull Optional<ScoreboardLine> getLine(int line);

    @NotNull Scoreboard setLine(int line, @NotNull String value);

    @NotNull Scoreboard removeLine(int line);

    void apply(@NotNull Player player);

    void remove(@NotNull Player player);

}