package net.seocraft.api.bukkit.board;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ScoreboardApplier {

    void apply(@NotNull Scoreboard scoreboard, @NotNull Player player);

}