package net.seocraft.commons.bukkit.board;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.creator.board.Scoreboard;
import net.seocraft.api.bukkit.creator.board.ScoreboardApplier;
import net.seocraft.api.bukkit.creator.board.ScoreboardManager;
import net.seocraft.api.bukkit.creator.board.ScoreboardRemover;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineCreator;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineRemover;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CraftScoreboardManager implements ScoreboardManager {

    @NotNull private List<Scoreboard> scoreboards;
    @Inject private ScoreboardApplier defaultApplier;
    @Inject private ScoreboardRemover defaultRemover;
    @Inject private ScoreboardLineCreator defaultLineCreator;
    @Inject private ScoreboardLineRemover defaultLineRemover;

    @Inject CraftScoreboardManager(@NotNull ArrayList<Scoreboard> scoreboards) {
        this.scoreboards = scoreboards;
    }

    @Override
    public @NotNull List<Scoreboard> getScoreboards() {
        return this.scoreboards;
    }

    @Override
    public @NotNull ScoreboardApplier getDefaultApplier() {
        return this.defaultApplier;
    }

    @Override
    public @NotNull ScoreboardRemover getDefaultRemover() {
        return this.defaultRemover;
    }

    @Override
    public @NotNull ScoreboardLineCreator getDefaultLineCreator() {
        return this.defaultLineCreator;
    }

    @Override
    public @NotNull ScoreboardLineRemover getDefaultLineRemover() {
        return this.defaultLineRemover;
    }

    @Override
    public @NotNull Optional<Scoreboard> getPlayerScoreboard(@NotNull String playerId) {
        return this.scoreboards.stream().filter(scoreboard -> scoreboard.getViewers().stream().anyMatch(player -> player.getUniqueId().toString().equals(playerId))).findAny();
    }

    @Override
    public @NotNull ScoreboardManager setDefaultApplier(@NotNull ScoreboardApplier applier) {
        this.defaultApplier = applier;
        return this;
    }

    @Override
    public @NotNull ScoreboardManager setDefaultRemover(@NotNull ScoreboardRemover remover) {
        this.defaultRemover = remover;
        return this;
    }

    @Override
    public @NotNull ScoreboardManager setDefaultLineCreator(@NotNull ScoreboardLineCreator lineCreator) {
        this.defaultLineCreator = lineCreator;
        return this;
    }

    @Override
    public @NotNull ScoreboardManager setDefaultLineRemover(@NotNull ScoreboardLineRemover lineRemover) {
        this.defaultLineRemover = lineRemover;
        return this;
    }

    @Override
    public @NotNull Scoreboard createScoreboard(@NotNull String title) {
        Scoreboard scoreboard = new CraftScoreboard(title, this.defaultApplier, this.defaultRemover, this.defaultLineCreator, this.defaultLineRemover);
        this.scoreboards.add(scoreboard);
        return scoreboard;
    }
}