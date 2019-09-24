package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.event.GameReadyEvent;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameStartManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.CountdownTimer;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class CraftGameStartManager implements GameStartManager {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private UserFormatter userFormatter;
    @Inject private TranslatableField translatableField;

    @Inject private BukkitAPI bukkitAPI;
    @Inject private CommonsBukkit instance;

    private Map<String, Integer> scheduledStarts = new HashMap<>();

    @Override
    public void startMatchCountdown(@NotNull Match match) {
        if (!this.scheduledStarts.containsKey(match.getId())) {
            CountdownTimer timer = new CountdownTimer(
                    this.instance,
                    30,
                    (time) -> this.coreGameManagement.getMatchUsers(match.getId()).forEach(user -> {
                        Player player = Bukkit.getPlayer(user.getUsername());
                        if (player != null) {
                            player.sendMessage(
                                    ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_start_countdown")
                                            .replace("%%left%%", ChatColor.GOLD + "" + time.getSecondsLeft() + ChatColor.YELLOW)
                            );
                        }
                    }),
                    () -> Bukkit.getPluginManager().callEvent(new GameReadyEvent(match.getId()))
            );
            timer.scheduleTimer();
            scheduledStarts.put(match.getId(), timer.getAssignedTaskId());
        }
    }

    @Override
    public void forceMatchCountdown(@NotNull Match match, int seconds, @NotNull User issuer, boolean silent) {
        CountdownTimer timer = new CountdownTimer(
                this.instance,
                seconds,
                () -> {
                    scheduledStarts.remove(match.getId());
                    this.coreGameManagement.getMatchUsers(match.getId()).forEach(user -> {
                        Player player = Bukkit.getPlayer(user.getUsername());
                        if (player != null) {
                            if (silent) {
                                player.sendMessage(
                                        ChatColor.GREEN  + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_forced_silent")
                                );
                            } else {
                                player.sendMessage(
                                        ChatColor.GREEN  + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_forced")
                                        .replace("%%player%%", this.userFormatter.getUserFormat(user, this.bukkitAPI.getConfig().getString("realm")) + ChatColor.GREEN)
                                );
                            }
                        }
                    });
                },
                (time) -> this.coreGameManagement.getMatchUsers(match.getId()).forEach(user -> {
                    Player player = Bukkit.getPlayer(user.getUsername());
                    if (player != null) {
                        player.sendMessage(
                                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_start_countdown")
                                        .replace("%%left%%", ChatColor.GOLD + "" + ChatColor.YELLOW)
                        );
                    }
                }),
                () -> Bukkit.getPluginManager().callEvent(new GameReadyEvent(match.getId()))
        );
        timer.scheduleTimer();
        scheduledStarts.put(match.getId(), timer.getAssignedTaskId());
    }

    @Override
    public void cancelMatchCountdown(@NotNull Match match) {
        if (this.scheduledStarts.containsKey(match.getId())) {
            Bukkit.getScheduler().cancelTask(this.scheduledStarts.get(match.getId()));
            this.scheduledStarts.remove(match.getId());
            this.coreGameManagement.getMatchUsers(match.getId()).forEach(user -> {
                Player player = Bukkit.getPlayer(user.getUsername());
                if (player != null) {
                    player.sendMessage(
                            ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_cancelled")
                    );
                }
            });
        }
    }

    @Override
    public void cancelMatchCountdown(@NotNull Match match, @NotNull User user, boolean silent) {
        Bukkit.getScheduler().cancelTask(this.scheduledStarts.get(match.getId()));
        this.coreGameManagement.getMatchUsers(match.getId()).forEach(matchUser -> {
            Player player = Bukkit.getPlayer(matchUser.getUsername());
            if (player != null) {
                if (silent) {
                    player.sendMessage(
                            ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_silent")
                    );
                } else {
                    player.sendMessage(
                            ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_cancelled_forced")
                            .replace("%%player%%", this.userFormatter.getUserFormat(user, this.bukkitAPI.getConfig().getString("realm")) + ChatColor.RED)
                    );
                }
            }
        });
    }
}
