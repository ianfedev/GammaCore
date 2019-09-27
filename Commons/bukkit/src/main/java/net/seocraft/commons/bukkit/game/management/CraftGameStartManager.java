package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.event.GameReadyEvent;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameStartManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.redis.RedisClient;
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

import java.util.Objects;
import java.util.Set;

@Singleton
public class CraftGameStartManager implements GameStartManager {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private UserFormatter userFormatter;
    @Inject private TranslatableField translatableField;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private CommonsBukkit instance;
    @Inject private RedisClient client;

    @NotNull private String scheduledField;

    @Inject
    public CraftGameStartManager(CommonsBukkit instance) {
        this.instance = instance;
        this.scheduledField = "scheduledStarts:" + this.instance.getServerRecord().getId();
    }

    @Override
    public void startMatchCountdown(@NotNull Match match) {

        if (!this.client.existsInHash(this.scheduledField, match.getId())) {

            Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
            involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));

            CountdownTimer timer = new CountdownTimer(
                    this.instance,
                    30,
                    (time) -> {
                        if (time.isImportantSecond()) involvedUsers.forEach(user -> this.sendCountdownAlert(Bukkit.getPlayer(user.getUsername()), time.getSecondsLeft(), user.getLanguage()));
                    },
                    () -> {
                        this.client.deleteHash(this.scheduledField, match.getId());
                        Bukkit.getPluginManager().callEvent(new GameReadyEvent(match));
                    }
            );
            timer.scheduleTimer();
            this.client.setHash(this.scheduledField, match.getId(), timer.getAssignedTaskId().toString());
        }
    }

    @Override
    public void forceMatchCountdown(@NotNull Match match, int seconds, @NotNull User issuer, boolean silent) {

        Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
        int matchUsers =  involvedUsers.size();
        involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));
        if (this.client.existsInHash(this.scheduledField, match.getId())) {
            Bukkit.getScheduler().cancelTask(Integer.parseInt(Objects.requireNonNull(this.client.getFromHash(this.scheduledField, match.getId()))));
            this.client.deleteHash(this.scheduledField, match.getId());
        }

        if (matchUsers >= 2) {
            CountdownTimer timer = new CountdownTimer(
                    this.instance,
                    seconds,
                    () -> involvedUsers.forEach(user -> {
                        Player player = Bukkit.getPlayer(user.getUsername());
                        if (player != null) {
                            if (silent) {
                                player.sendMessage(
                                        ChatColor.GREEN  + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_forced_silent")
                                );
                            } else {
                                player.sendMessage(
                                        ChatColor.GREEN  + this.translatableField.getUnspacedField(user.getLanguage(), "commons_countdown_forced")
                                                .replace("%%player%%", this.userFormatter.getUserFormat(issuer, this.bukkitAPI.getConfig().getString("realm")) + ChatColor.GREEN)
                                );
                            }
                        }
                    }),
                    (time) -> {
                        if (time.isImportantSecond()) this.coreGameManagement.getMatchUsers(match.getId()).forEach(user -> this.sendCountdownAlert(Bukkit.getPlayer(user.getUsername()), time.getSecondsLeft(), user.getLanguage()));
                    },
                    () -> {
                        this.client.deleteHash(this.scheduledField, match.getId());
                        Bukkit.getPluginManager().callEvent(new GameReadyEvent(match));
                    }
            );
            this.client.setHash(this.scheduledField, match.getId(), timer.getAssignedTaskId().toString());
            timer.scheduleTimer();
        } else {
            Player player = Bukkit.getPlayer(issuer.getUsername());
            ChatAlertLibrary.errorChatAlert(
                    player,
                    this.translatableField.getUnspacedField(
                            issuer.getLanguage(),
                            "commons_countdown_insufficient"
                    )
            );
        }
    }

    @Override
    public void cancelMatchCountdown(@NotNull Match match) {
        if (this.client.existsInHash(this.scheduledField, match.getId())) {
            Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
            involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));
            Bukkit.getScheduler().cancelTask(Integer.parseInt(Objects.requireNonNull(this.client.getFromHash(this.scheduledField, match.getId()))));
            this.client.deleteHash(this.scheduledField, match.getId());
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
        if (this.client.existsInHash(this.scheduledField, match.getId())) {
            Bukkit.getScheduler().cancelTask(Integer.parseInt(Objects.requireNonNull(this.client.getFromHash(this.scheduledField, match.getId()))));
            this.client.deleteHash(this.scheduledField, match.getId());
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