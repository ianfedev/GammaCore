package net.seocraft.api.bukkit.creator.board;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ScoreboardRemover {

    void remove(@NotNull Scoreboard scoreboard, @NotNull Player player);

}