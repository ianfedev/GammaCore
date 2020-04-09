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
import net.seocraft.api.bukkit.utils.CountdownTimer;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Singleton
public class CraftGameStartManager implements GameStartManager {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private UserFormatter userFormatter;
    @Inject private TranslatableField translatableField;
    @Inject private CommonsBukkit instance;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private RedisClient client;

    @Override
    public void startMatchCountdown(@NotNull Match match) {

        if (!this.client.getHashFields(getScheduledString()).containsKey(match.getId())) {

            Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
            involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));

            CountdownTimer timer = new CountdownTimer(
                    this.instance,
                    30,
                    (time) -> {
                        this.coreGameManagement.updateMatchRemaingTime(match.getId(), time.getSecondsLeft());
                        if (time.isImportantSecond()) involvedUsers.forEach(user -> this.sendCountdownAlert(Bukkit.getPlayer(user.getUsername()), time.getSecondsLeft(), user.getLanguage()));
                    },
                    () -> startMatch(match)
            );
            timer.scheduleTimer();
            if (timer.getAssignedTaskId() != null) this.client.setHash(getScheduledString(), match.getId(), timer.getAssignedTaskId().toString());
        }
    }

    @Override
    public void forceMatchCountdown(@NotNull Match match, int seconds, @NotNull User issuer, boolean silent) {

        Map<String, String> temporalCountdown = this.client.getHashFields(getScheduledString());

        Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
        int matchUsers =  involvedUsers.size();
        involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));
        if (temporalCountdown.containsKey(match.getId())) {
            int cancellableTask = Integer.parseInt(temporalCountdown.get(match.getId()));
            Bukkit.getScheduler().cancelTask(cancellableTask);
            this.client.deleteHash(getScheduledString(), match.getId());
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
                        this.coreGameManagement.updateMatchRemaingTime(match.getId(), time.getSecondsLeft());
                        if (time.isImportantSecond()) this.coreGameManagement.getMatchUsers(match.getId()).forEach(user -> this.sendCountdownAlert(Bukkit.getPlayer(user.getUsername()), time.getSecondsLeft(), user.getLanguage()));
                    },
                    () -> startMatch(match)
            );
            timer.scheduleTimer();
            if (timer.getAssignedTaskId() != null) this.client.setHash(getScheduledString(), match.getId(), timer.getAssignedTaskId().toString());
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
        Map<String, String> temporalCountdown = this.client.getHashFields(getScheduledString());
        if (temporalCountdown.containsKey(match.getId())) {
            Set<User> involvedUsers = this.coreGameManagement.getMatchUsers(match.getId());
            involvedUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));
            int taskId = Integer.parseInt(temporalCountdown.get(match.getId()));
            Bukkit.getScheduler().cancelTask(taskId);
            this.client.deleteHash(getScheduledString(), match.getId());
            this.coreGameManagement.updateMatchRemaingTime(match.getId(), -1);
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

        Map<String, String> temporalCountdown = this.client.getHashFields(getScheduledString());

        if (temporalCountdown.containsKey(match.getId())) {

            int taskId = Integer.parseInt(temporalCountdown.get(match.getId()));

            Bukkit.getScheduler().cancelTask(taskId);
            this.client.deleteHash(getScheduledString(), match.getId());
            this.coreGameManagement.updateMatchRemaingTime(match.getId(), -1);
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

    @Override
    public void kickErrorPlayers(Match match) {
        this.coreGameManagement.getMatchUsers(match.getId()).forEach(user -> {
            Player player = Bukkit.getPlayer(user.getUsername());
            if (player != null) {
                player.kickPlayer(
                        ChatColor.RED +
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "gamelayout_error_starting"
                                )
                );
            }
        });
    }

    private void startMatch(Match match) {
        try {
            this.client.deleteHash(getScheduledString(), match.getId());
            match.setStatus(MatchStatus.STARTING);
            this.coreGameManagement.updateMatch(match);
            Bukkit.getPluginManager().callEvent(new GameReadyEvent(match));
            this.coreGameManagement.getMatchPlayers(match.getId()).forEach(player -> this.coreGameManagement.getWaitingPlayers().remove(player));
        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
            this.kickErrorPlayers(match);
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

    private String getScheduledString() {
        return "scheduledStarts:" + this.instance.getServerRecord().getId();
    }

}