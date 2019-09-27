package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.event.GameReadyEvent;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameStartManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.bukkit.util.CountdownTimer;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

            Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
            involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));

            CountdownTimer timer = new CountdownTimer(
                    this.instance,
                    30,
                    (time) -> involvedUsers.forEach(user -> this.sendCountdownAlert(Bukkit.getPlayer(user.getUsername()), time.getSecondsLeft(), user.getLanguage())),
                    () -> {
                        scheduledStarts.remove(match.getId());
                        Bukkit.getPluginManager().callEvent(new GameReadyEvent(match));
                    }
            );
            timer.scheduleTimer();
            scheduledStarts.put(match.getId(), timer.getAssignedTaskId());
        }
    }

    @Override
    public void forceMatchCountdown(@NotNull Match match, int seconds, @NotNull User issuer, boolean silent) {

        Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
        involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));

        CountdownTimer timer = new CountdownTimer(
                this.instance,
                seconds,
                () -> {
                    if (this.scheduledStarts.containsKey(match.getId())) Bukkit.getScheduler().cancelTask(this.scheduledStarts.get(match.getId()));
                    involvedUsers.forEach(user -> {
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
                (time) -> this.coreGameManagement.getMatchUsers(match.getId()).forEach(user -> this.sendCountdownAlert(Bukkit.getPlayer(user.getUsername()), time.getSecondsLeft(), user.getLanguage())),
                () -> {
                    scheduledStarts.remove(match.getId());
                    Bukkit.getPluginManager().callEvent(new GameReadyEvent(match));
                }
        );
        timer.scheduleTimer();
        scheduledStarts.put(match.getId(), timer.getAssignedTaskId());
    }

    @Override
    public void cancelMatchCountdown(@NotNull Match match) {
        if (this.scheduledStarts.containsKey(match.getId())) {
            Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
            involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));
            Bukkit.getScheduler().cancelTask(this.scheduledStarts.get(match.getId()));
            this.scheduledStarts.remove(match.getId());
            involvedUsers.forEach(user -> {
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
        if (this.scheduledStarts.containsKey(match.getId())) {
            Bukkit.getScheduler().cancelTask(this.scheduledStarts.get(match.getId()));
            this.scheduledStarts.remove(match.getId());
            this.coreGameManagement.getMatchUsers(match.getId()).forEach(matchUser -> {
                Player player = Bukkit.getPlayer(matchUser.getUsername());
                if (player != null) {
                    if (silent) {
                        player.sendMessage(
                                ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_silent") + "."
                        );
                    } else {
                        player.sendMessage(
                                ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_cancelled_forced")
                                        .replace("%%player%%", this.userFormatter.getUserFormat(user, this.bukkitAPI.getConfig().getString("realm")) + ChatColor.RED) + "."
                        );
                    }
                }
            });
        } else {
            Player player = Bukkit.getPlayer(user.getUsername());
            if (player != null) ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_not_started"));
        }
    }

    private void sendCountdownAlert(@Nullable Player player, int seconds, @NotNull String language) {
        if (player != null) {
            player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
            player.sendMessage(
                    ChatColor.YELLOW + this.translatableField.getUnspacedField(language, "commons_start_countdown")
                            .replace("%%left%%", ChatColor.GOLD + "" + seconds + ChatColor.YELLOW)
            );
        }
    }
}