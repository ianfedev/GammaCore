package net.seocraft.commons.bukkit.board;

import net.seocraft.api.bukkit.creator.board.Scoreboard;
import net.seocraft.api.bukkit.creator.board.ScoreboardApplier;
import net.seocraft.api.bukkit.creator.board.ScoreboardRemover;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLine;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineCreator;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineRemover;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CraftScoreboard implements Scoreboard {

    @NotNull private String title;
    @NotNull private List<Player> viewers;
    @NotNull private List<ScoreboardLine> lines;
    @NotNull private ScoreboardApplier applier;
    @NotNull private ScoreboardRemover remover;
    @NotNull private ScoreboardLineCreator lineCreator;
    @NotNull private ScoreboardLineRemover lineRemover;

    private static final List<ChatColor> colors = Arrays.asList(ChatColor.values());

    public CraftScoreboard(@NotNull String title, @NotNull ScoreboardApplier applier, @NotNull ScoreboardRemover remover, @NotNull ScoreboardLineCreator lineCreator, @NotNull ScoreboardLineRemover lineRemover) {
        this.title = title;
        this.viewers = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.applier = applier;
        this.remover = remover;
        this.lineCreator = lineCreator;
        this.lineRemover = lineRemover;
    }

    @Override
    public @NotNull String getTitle() {
        return this.title;
    }

    @Override
    public @NotNull List<Player> getViewers() {
        return this.viewers;
    }

    @Override
    public @NotNull List<ScoreboardLine> getLines() {
        return this.lines;
    }

    @Override
    public @NotNull ScoreboardApplier getApplier() {
        return this.applier;
    }

    @Override
    public @NotNull ScoreboardRemover getRemover() {
        return this.remover;
    }

    @Override
    public @NotNull ScoreboardLineCreator getLineCreator() {
        return this.lineCreator;
    }

    @Override
    public @NotNull ScoreboardLineRemover getLineRemover() {
        return this.lineRemover;
    }

    @Override
    public @NotNull Optional<ScoreboardLine> getLine(int line) {
        return lines.stream().filter(scoreboardLine -> scoreboardLine.getPlace() == line).findAny();
    }

    @Override
    public @NotNull Scoreboard setLine(int line, @NotNull String value) {
        this.removeLine(line);
        this.lines.add(this.lineCreator.create(this, line, value));
        return this;
    }

    @Override
    public @NotNull Scoreboard removeLine(int line) {
        this.getLine(line).ifPresent(scoreboardLine ->  {
            this.lineRemover.remove(this, scoreboardLine);
        });
        return this;
    }

    @Override
    public void apply(@NotNull Player player) {
        this.viewers.add(player);
        this.applier.apply(this, player);
    }

    @Override
    public void remove(@NotNull Player player) {
        this.viewers.remove(player);
        this.remover.remove(this, player);
    }
}