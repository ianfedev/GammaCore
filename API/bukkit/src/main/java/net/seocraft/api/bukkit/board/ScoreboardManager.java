package net.seocraft.api.bukkit.board;


import net.seocraft.api.bukkit.board.line.ScoreboardLineCreator;
import net.seocraft.api.bukkit.board.line.ScoreboardLineRemover;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface ScoreboardManager {

    @NotNull List<Scoreboard> getScoreboards();

    @NotNull ScoreboardApplier getDefaultApplier();

    @NotNull ScoreboardRemover getDefaultRemover();

    @NotNull ScoreboardLineCreator getDefaultLineCreator();

    @NotNull ScoreboardLineRemover getDefaultLineRemover();

    @NotNull Optional<Scoreboard> getPlayerScoreboard(@NotNull String playerId);

    @NotNull ScoreboardManager setDefaultApplier(@NotNull ScoreboardApplier applier);

    @NotNull ScoreboardManager setDefaultRemover(@NotNull ScoreboardRemover remover);

    @NotNull ScoreboardManager setDefaultLineCreator(@NotNull ScoreboardLineCreator lineCreator);

    @NotNull ScoreboardManager setDefaultLineRemover(@NotNull ScoreboardLineRemover lineRemover);

    @NotNull Scoreboard createScoreboard(@NotNull String title);

}