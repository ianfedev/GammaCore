package net.seocraft.api.bukkit.creator.board;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ScoreboardApplier {

    void apply(@NotNull Scoreboard scoreboard, @NotNull Player player);

}