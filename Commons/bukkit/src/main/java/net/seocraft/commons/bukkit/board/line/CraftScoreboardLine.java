package net.seocraft.commons.bukkit.board.line;

import net.seocraft.api.bukkit.creator.board.line.ScoreboardLine;
import org.jetbrains.annotations.NotNull;

public class CraftScoreboardLine implements ScoreboardLine {

    private int place;
    @NotNull private String entry;
    @NotNull private String text;

    public CraftScoreboardLine(int place, @NotNull String entry, @NotNull String text) {
        this.entry = entry;
        this.text = text;
        this.place = place;
    }

    @Override
    public int getPlace() {
        return this.place;
    }

    @Override
    public @NotNull String getEntry() {
        return this.entry;
    }

    @Override
    public @NotNull String getText() {
        return this.text;
    }

    @Override
    public void setEntry(@NotNull String entry) {
        this.entry = entry;
    }

    @Override
    public void setText(@NotNull String text) {
        this.entry = entry;
    }
}