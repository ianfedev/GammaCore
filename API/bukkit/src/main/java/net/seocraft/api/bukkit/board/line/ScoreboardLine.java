package net.seocraft.api.bukkit.board.line;

import org.jetbrains.annotations.NotNull;

public interface ScoreboardLine {

    int getPlace();

    @NotNull String getEntry();

    @NotNull String getText();

    void setEntry(@NotNull String entry);

    void setText(@NotNull String text);

}