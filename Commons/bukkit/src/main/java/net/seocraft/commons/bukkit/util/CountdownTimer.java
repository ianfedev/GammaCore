package net.seocraft.commons.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CountdownTimer implements Runnable {

    @NotNull private JavaPlugin plugin;
    @Nullable private Integer assignedTaskId;

    private int seconds;
    private int secondsLeft;

    @NotNull private Consumer<CountdownTimer> everySecond;
    @Nullable private Runnable beforeTimer;
    @Nullable private Runnable afterTimer;

    public CountdownTimer(@NotNull JavaPlugin plugin, int seconds, @Nullable Runnable beforeTimer, @NotNull Consumer<CountdownTimer> everySecond, @Nullable Runnable afterTimer) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    public CountdownTimer(@NotNull JavaPlugin plugin, int seconds, @NotNull Consumer<CountdownTimer> everySecond, @Nullable Runnable afterTimer) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.beforeTimer = null;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    public CountdownTimer(@NotNull JavaPlugin plugin, int seconds, @Nullable Runnable beforeTimer, @NotNull Consumer<CountdownTimer> everySecond) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.beforeTimer = beforeTimer;
        this.afterTimer = null;
        this.everySecond = everySecond;
    }

    public CountdownTimer(@NotNull JavaPlugin plugin, int seconds, @NotNull Consumer<CountdownTimer> everySecond) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.beforeTimer = null;
        this.afterTimer = null;
        this.everySecond = everySecond;
    }

    @Override
    public void run() {
        if (secondsLeft < 1) {
            if (afterTimer != null) afterTimer.run();
            if (assignedTaskId != null) Bukkit.getScheduler().cancelTask(assignedTaskId);
            return;
        }

        if (secondsLeft == seconds && beforeTimer != null) beforeTimer.run();

        everySecond.accept(this);

        secondsLeft--;
    }

    public int getTotalSeconds() {
        return seconds;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public Integer getAssignedTaskId() {
        return assignedTaskId;
    }

    public void cancelCountdown() {
        Bukkit.getScheduler().cancelTask(assignedTaskId);
    }

    public void scheduleTimer() {
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }

    public boolean isImportantSecond() {
        return ((this.secondsLeft % 15) == 0) || (this.secondsLeft < 11 && this.secondsLeft >= 1);
    }

}