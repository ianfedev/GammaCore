package net.seocraft.api.bukkit.board;

import net.seocraft.api.bukkit.board.effect.GlowingRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftGameBoard;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.GameBoardException;
import org.jetbrains.annotations.NotNull;

public class LightingAnimatedBoard extends CraftGameBoard {

    private int task;
    private @NotNull Plugin plugin;

    public LightingAnimatedBoard(@NotNull String title, @NotNull Plugin plugin) {
        super(title);
        if (title.contains(ChatColor.COLOR_CHAR + "")) throw new GameBoardException("You can not use colors at this lightning scoreboard");
        this.plugin = plugin;
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, new GlowingRunnable(this.getObjective(), this.getTitle()), 0, 5L).getTaskId();
    }

    @Override
    public void setTitle(@NotNull String title) {
        if (title.contains(ChatColor.COLOR_CHAR + "")) throw new GameBoardException("You can not use colors at this lightning scoreboard");
        super.setTitle(title);
        Bukkit.getScheduler().cancelTask(this.task);
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, new GlowingRunnable(this.getObjective(), this.getTitle()), 0, 5L).getTaskId();
    }

}
