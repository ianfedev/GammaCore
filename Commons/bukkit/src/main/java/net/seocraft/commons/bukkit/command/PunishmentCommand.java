package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.*;
import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.bukkit.punishment.PunishmentProvider;
import net.seocraft.api.bukkit.punishment.PunishmentType;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserExpulsion;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.utils.TimeUtils;
import net.seocraft.commons.bukkit.punishment.BridgedUserBan;
import net.seocraft.commons.bukkit.punishment.PunishmentActions;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;

public class PunishmentCommand implements CommandClass {

    @Inject private TranslatableField translator;
    @Inject private OnlineStatusManager onlinePlayers;
    @Inject private PunishmentProvider punishmentProvider;
    @Inject private Messager messager;
    @Inject private PunishmentActions punishmentActions;
    @Inject private UserStorageProvider userStorageProvider;

    @ACommand(names = {"ban", "tempban", "suspender", "st", "tb", "tban", "pban", "sp"}, permission = "commons.staff.punish")
    public boolean banCommand(@Injected(true) @Sender Player player, OfflinePlayer target, @Default("") String duration, @Default("") @ConsumedArgs(-1) String reason, @Flag('s') boolean silent) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                //Detecting auto punishment
                if (player.equals(target)) {
                    ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                            user.getLanguage(), "commons_punish_yourself"
                    ) + ".");
                    return;
                }

                // Get online player data
                CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                    if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User targetRecord = targetAsyncResponse.getResponse();
                        String serverName = Bukkit.getServerName();

                        // Detecting if player is online
                        if (isTargetOffline(player, targetRecord.getId(), user.getLanguage())) return;

                        // Check if user has lower priority
                        if (hasLowerPermissions(user, targetRecord, player)) return;

                        // Create punishment if reason or duration is provided
                        if (duration.isEmpty() && reason.isEmpty()) {

                            if (!player.hasPermission("commons.staff.punish.permaban")) {
                                ChatAlertLibrary.errorChatAlert(
                                        player,
                                        this.translator.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_insufficient_permissions"
                                        ) + "."
                                );
                                return;
                            }

                            try {
                                Punishment punishment = this.punishmentProvider.createPunishment(
                                        PunishmentType.BAN,
                                        user.getId(),
                                        targetRecord.getId(),
                                        "unknown",
                                        null,
                                        getPlayerIP((Player) target),
                                        this.translator.getField(targetRecord.getLanguage(), "commons_punish_ban")
                                                + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase(),
                                        -1,
                                        false,
                                        silent
                                );
                                BridgedUserBan.banPlayer(
                                        this.messager.getChannel("proxyBan", UserExpulsion.class),
                                        punishment,
                                        targetRecord
                                );
                            } catch (Unauthorized | BadRequest | InternalServerError | NotFound | IOException unauthorized) {
                                ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_punish_error") + ".");
                            }
                            return;
                        }

                        //Parsing first argument as provided date
                        long millisDuration;
                        try {
                            millisDuration = TimeUtils.parseDuration(duration);
                        } catch (NumberFormatException ex) {
                            millisDuration = 0L;
                        }

                        if (millisDuration > 0) {
                            if (!player.hasPermission("commons.staff.punish.tempban")) {
                                ChatAlertLibrary.errorChatAlert(
                                        player,
                                        this.translator.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_insufficient_permissions"
                                        ) + "."
                                );
                                return;
                            }

                            String banReason = this.translator.getField(targetRecord.getLanguage(), "commons_punish_ban")
                                    + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase();

                            if (!reason.isEmpty()) {
                                banReason = reason;
                            }

                            long expirationDate = TimeUtils.getUnixStamp(ZonedDateTime.now().plus(
                                    Duration.ofMillis(millisDuration)
                            ));
                            try {
                                Punishment punishment = this.punishmentProvider.createPunishment(
                                        PunishmentType.BAN,
                                        user.getId(),
                                        targetRecord.getId(),
                                        serverName,
                                        null,
                                        getPlayerIP((Player) target),
                                        banReason,
                                        expirationDate,
                                        false,
                                        silent
                                );
                                BridgedUserBan.banPlayer(
                                        this.messager.getChannel("proxyBan", UserExpulsion.class),
                                        punishment,
                                        targetRecord
                                );
                            } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException unauthorized) {
                                ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_punish_error") + ".");
                            }
                            return;
                        }

                        // Duration is added as part of reason if invalid
                        if (!player.hasPermission("commons.staff.punish.permaban")) {
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translator.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_insufficient_permissions" + "."
                                    )
                            );
                            return;
                        }

                        String banReason = duration + reason;
                        try {
                            Punishment punishment = this.punishmentProvider.createPunishment(
                                    PunishmentType.BAN,
                                    user.getId(),
                                    targetRecord.getId(),
                                    serverName,
                                    null,
                                    getPlayerIP((Player) target),
                                    banReason,
                                    -1,
                                    false,
                                    silent
                            );
                            BridgedUserBan.banPlayer(
                                    this.messager.getChannel("proxyBan", UserExpulsion.class),
                                    punishment,
                                    targetRecord
                            );
                        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException unauthorized) {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_punish_error") + ".");
                        }
                    } else {
                        if (targetAsyncResponse.getStatusCode() == 404) {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_not_found") + ".");
                        } else {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_punish_error") + ".");
                        }
                    }
                });

            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }
        });

        return true;
    }

    @ACommand(names = {"kick", "expulsar"}, permission = "commons.staff.kick")
    public boolean kickCommand(@Injected(true) @Sender Player player, OfflinePlayer target, @Default("") @ConsumedArgs(-1) String reason, @Flag('s') boolean silent) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                //Detecting auto punishment
                if (player.equals(target)) {
                    ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                            user.getLanguage(), "commons_punish_yourself"
                    ) + ".");
                    return;
                }

                // Get online player data
                CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                    if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User targetRecord = targetAsyncResponse.getResponse();
                        String serverName = Bukkit.getServerName();

                        // Detecting if player is online
                        if (isTargetOffline(player, targetRecord.getId(), user.getLanguage())) return;

                        // Check if user has lower priority
                        if (hasLowerPermissions(user, targetRecord, player)) return;

                        String kickReason = this.translator.getField(targetRecord.getLanguage(), "commons_punish_kick")
                                + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase();

                        if (!reason.isEmpty()) {
                            kickReason = reason;
                        }

                        try {
                            Punishment punishment = this.punishmentProvider.createPunishment(
                                    PunishmentType.KICK,
                                    user.getId(),
                                    targetRecord.getId(),
                                    serverName,
                                    null,
                                    getPlayerIP((Player) target),
                                    kickReason,
                                    -1,
                                    false,
                                    silent
                            );
                            this.punishmentActions.kickPlayer(target.getPlayer(), targetRecord, punishment);
                        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException unauthorized) {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_punish_error") + ".");
                        }
                    } else {
                        if (targetAsyncResponse.getStatusCode() == 404) {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_not_found") + ".");
                        } else {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_punish_error") + ".");
                        }
                    }
                });
            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }
        });

        return true;
    }

    @ACommand(names = {"warn", "advertir"}, permission = "commons.staff.warn")
    public boolean warnCommand(@Injected(true) @Sender Player player, OfflinePlayer target, @Default("") @ConsumedArgs(-1) String reason, @Flag('s') boolean silent) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                //Detecting auto punishment
                if (player.equals(target)) {
                    ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                            user.getLanguage(), "commons_punish_yourself"
                    ) + ".");
                    return;
                }

                // Get online player data
                CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                    if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User targetRecord = targetAsyncResponse.getResponse();
                        String serverName = Bukkit.getServerName();

                        // Detecting if player is online
                        if (isTargetOffline(player, targetRecord.getId(), user.getLanguage())) return;

                        // Check if user has lower priority
                        if (hasLowerPermissions(user, targetRecord, player)) return;

                        String warnReason = this.translator.getField(targetRecord.getLanguage(), "commons_punish_warn")
                                + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase();
                        if (!reason.isEmpty()){
                            warnReason = reason;
                        }

                        try {
                            Punishment punishment = this.punishmentProvider.createPunishment(
                                    PunishmentType.WARN,
                                    user.getId(),
                                    targetRecord.getId(),
                                    serverName,
                                    null,
                                    getPlayerIP((Player) target),
                                    warnReason,
                                    -1,
                                    false,
                                    silent
                            );
                            this.punishmentActions.warnPlayer(target.getPlayer(), targetRecord, punishment);
                        } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_punish_error") + ".");
                        } catch (IOException e) {
                            ChatAlertLibrary.errorChatAlert(player);
                        }
                    } else {
                        if (targetAsyncResponse.getStatusCode() == 404) {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_not_found") + ".");
                        } else {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_punish_error") + ".");
                        }
                    }
                });

            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }
        });
        return true;
    }

    private @NotNull String getPlayerIP(@NotNull Player player) {
        return player.getAddress().toString().split(":")[0].replace("/", "");
    }

    private boolean hasLowerPermissions(User user, User target, Player player) {
        if (user.getPrimaryGroup().getPriority() > target.getPrimaryGroup().getPriority()) {
            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                    user.getLanguage(),
                    "commons_punish_lower_permissions") + ".");
            return true;
        } else {
            return false;
        }
    }

    private boolean isTargetOffline(Player player, String id, String language) {
        if (!this.onlinePlayers.isPlayerOnline(id)) {
            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                    language, "commons_punish_offline").replace("%%url%%",
                    ChatColor.YELLOW + "https://www.seocraft.net" + ChatColor.RED + "."
            ));
            return true;
        } else {
            return false;
        }
    }

}