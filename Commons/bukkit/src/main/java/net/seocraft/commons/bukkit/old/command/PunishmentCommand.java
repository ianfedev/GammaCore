package net.seocraft.commons.bukkit.old.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Parameter;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.commons.core.utils.TimeUtils;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.old.punishment.*;
import net.seocraft.commons.bukkit.old.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.ZonedDateTime;

public class PunishmentCommand implements CommandClass {

    @Inject private TranslatableField translator;
    @Inject private OnlineStatusManager onlinePlayers;
    @Inject private PunishmentHandler punishmentHandler;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private PunishmentActions punishmentActions;
    @Inject private UserStorageProvider userStorageProvider;

    @Command(names = {"ban", "tempban", "suspender", "st", "tb", "tban", "pban", "sp"}, permission = "commons.staff.punish", min = 1, usage = "/<command> <target> [duration] [reason] [-s]")
    public boolean banCommand(CommandSender sender, CommandContext context, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameSession playerSession = this.gameSessionManager.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Detecting auto punishment
                    if (player.getName().equalsIgnoreCase(context.getArgument(0))) {
                        ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                user.getLanguage(), "commons_punish_yourself"
                        ) + ".");
                        return;
                    }

                    // Get online player data
                    CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();
                            String serverName = "test"; //TODO: Get server from Cloud API

                            // Detecting if player is online
                            if (isTargetOffline(player, targetRecord.id(), user.getLanguage())) return;

                            // Get player session
                            GameSession targetSession = this.gameSessionManager.getCachedSession(target.getName());

                            // Check if user has lower priority
                            if (hasLowerPermissions(user, targetRecord, player)) return;

                            // Create punishment if reason or duration is provided
                            if (context.getArgumentsLength() == 1) {

                                if (!player.hasPermission("commons.staff.punish.permaban")) {
                                    ChatAlertLibrary.errorChatAlert(
                                            player,
                                            this.translator.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_insufficient_permissions"
                                            )  + "."
                                    );
                                    return;
                                }

                                try {
                                    Punishment punishment = this.punishmentHandler.createPunishment(
                                            PunishmentType.BAN,
                                            user.id(),
                                            targetRecord.id(),
                                            "unknown",
                                            null,
                                            targetSession.getAddress(),
                                            this.translator.getField(targetRecord.getLanguage(), "commons_punish_ban")
                                                    + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase(),
                                            -1,
                                            false,
                                            silent
                                    );
                                    this.punishmentActions.banPlayer(target.getPlayer(), targetRecord, punishment);
                                                                    } catch (Unauthorized | BadRequest | InternalServerError | NotFound unauthorized) {
                                    ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_punish_error") + ".");
                                }
                                return;
                            }

                            //Parsing first argument as provided date
                            String stringDuration = context.getArgument(1);
                            long millisDuration;
                            try {
                                millisDuration = TimeUtils.parseDuration(stringDuration);
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
                                if (context.getArgumentsLength() > 2) banReason = context.getJoinedArgs(2);
                                long expirationDate = TimeUtils.getUnixStamp(ZonedDateTime.now().plus(
                                        Duration.ofMillis(millisDuration)
                                ));
                                try {
                                    Punishment punishment = this.punishmentHandler.createPunishment(
                                            PunishmentType.BAN,
                                            user.id(),
                                            targetRecord.id(),
                                            serverName,
                                            null,
                                            targetSession.getAddress(),
                                            banReason,
                                            expirationDate,
                                            false,
                                            silent
                                    );
                                    this.punishmentActions.banPlayer(target.getPlayer(), targetRecord, punishment);
                                } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
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

                            String banReason = context.getJoinedArgs(1);
                            try {
                                Punishment punishment = this.punishmentHandler.createPunishment(
                                        PunishmentType.BAN,
                                        user.id(),
                                        targetRecord.id(),
                                        serverName,
                                        null,
                                        targetSession.getAddress(),
                                        banReason,
                                        -1,
                                        false,
                                        silent
                                );
                                this.punishmentActions.banPlayer(target.getPlayer(), targetRecord, punishment);
                            } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
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
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            });
        }
        return true;
    }

    @Command(names = {"kick", "expulsar"}, permission = "commons.staff.kick", min = 1, usage = "/<command> <target> [reason] [-s]")
    public boolean kickCommand(CommandSender sender, CommandContext context, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameSession playerSession = this.gameSessionManager.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Detecting auto punishment
                    if (player.getName().equalsIgnoreCase(context.getArgument(0))) {
                        ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                user.getLanguage(), "commons_punish_yourself"
                        ) + ".");
                        return;
                    }

                    // Get online player data
                    CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();
                            String serverName = "test"; //TODO: Get server from Cloud API

                            // Detecting if player is online
                            if (isTargetOffline(player, targetRecord.id(), user.getLanguage())) return;

                            // Get player session
                            GameSession targetSession = this.gameSessionManager.getCachedSession(target.getName());

                            // Check if user has lower priority
                            if (hasLowerPermissions(user, targetRecord, player)) return;

                            String reason = this.translator.getField(targetRecord.getLanguage(), "commons_punish_kick")
                                    + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase();
                            if (context.getArgumentsLength() > 1) reason = context.getJoinedArgs(1);

                            try {
                                Punishment punishment = this.punishmentHandler.createPunishment(
                                        PunishmentType.KICK,
                                        user.id(),
                                        targetRecord.id(),
                                        serverName,
                                        null,
                                        targetSession.getAddress(),
                                        reason,
                                        -1,
                                        false,
                                        silent
                                );
                                this.punishmentActions.kickPlayer(target.getPlayer(), targetRecord, punishment);
                            } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
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
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            });
        }
        return true;
    }

    @Command(names = {"warn", "advertir"}, permission = "commons.staff.warn", min = 1, usage = "/<command> <target> [reason] [-s]")
    public boolean warnCommand(CommandSender sender, CommandContext context, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(this.gameSessionManager.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Detecting auto punishment
                    if (player.getName().equalsIgnoreCase(context.getArgument(0))) {
                        ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                user.getLanguage(), "commons_punish_yourself"
                        ) + ".");
                        return;
                    }

                    // Get online player data
                    CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();
                            String serverName = "test"; //TODO: Get server from Cloud API

                            // Detecting if player is online
                            if (isTargetOffline(player, targetRecord.id(), user.getLanguage())) return;

                            // Get player session
                            GameSession targetSession = this.gameSessionManager.getCachedSession(target.getName());

                            // Check if user has lower priority
                            if (hasLowerPermissions(user, targetRecord, player)) return;

                            String reason = this.translator.getField(targetRecord.getLanguage(), "commons_punish_warn")
                                    + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase();
                            if (context.getArgumentsLength() > 1) reason = context.getJoinedArgs(1);

                            try {
                                Punishment punishment = this.punishmentHandler.createPunishment(
                                        PunishmentType.WARN,
                                        user.id(),
                                        targetRecord.id(),
                                        serverName,
                                        null,
                                        targetSession.getAddress(),
                                        reason,
                                        -1,
                                        false,
                                        silent
                                );
                                this.punishmentActions.warnPlayer(target.getPlayer(), targetRecord, punishment);
                            } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
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
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            });
        }
        return true;
    }

    private boolean hasLowerPermissions(User user, User target, Player player) {
        if (user.getPrimaryGroup().getPriority() > target.getPrimaryGroup().getPriority()) {
            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                    user.getLanguage(),
                    "commons_punish_lower_permissions")  + ".");
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
