package net.seocraft.commons.bukkit.commands;

import com.google.inject.Inject;
import me.ggamer55.bcm.CommandContext;
import me.ggamer55.bcm.parametric.CommandClass;
import me.ggamer55.bcm.parametric.annotation.Command;
import me.ggamer55.bcm.parametric.annotation.Parameter;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.onlineplayers.OnlinePlayersApi;
import net.seocraft.api.shared.serialization.TimeUtils;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.punishment.PunishmentHandler;
import net.seocraft.commons.bukkit.punishment.PunishmentType;
import net.seocraft.commons.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.ZonedDateTime;

public class PunishmentCommand implements CommandClass {

    @Inject private CommonsBukkit instance;
    @Inject private TranslatableField translator;
    @Inject private OnlinePlayersApi onlinePlayers;
    @Inject private PunishmentHandler punishmentHandler;
    @Inject private UserStoreHandler userStoreHandler;

    //TODO: Remove duplicated code
    //TODO: Change default answer of "NotFound"

    @Command(names = {"ban", "tempban", "suspender", "st", "tb", "tban", "pban", "sp"}, permission = "commons.staff.punish", min = 1, usage = "/<command> <target> [duration] [reason] [-s]")
    public boolean banCommand(CommandSender sender, CommandContext context, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.instance.playerIdentifier.get(player.getUniqueId())), userAsyncResponse -> {
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
                    CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.instance.playerIdentifier.get(target.getUniqueId())), targetAsyncResponse -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();
                            String serverName = "test"; //TODO: Get server from Cloud API

                            // Detecting if player is online
                            if (this.onlinePlayers.isPlayerOnline(targetRecord.id())) {
                                ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(), "commons_punish_offline".replace("%%url%%",
                                                ChatColor.YELLOW + "https://www.seocraft.net" + ChatColor.RED + "."
                                        )));
                                return;
                            }

                            // Check if user has lower priority
                            if (user.getPrimaryGroup().getPriority() > targetRecord.getPrimaryGroup().getPriority()) {
                                alertLowerPermissions(player, user.getLanguage());
                                return;
                            }

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
                                    this.punishmentHandler.createPunishment(
                                            PunishmentType.BAN,
                                            user.id(),
                                            targetRecord.id(),
                                            "unknown",
                                            null,
                                            getFormattedIp(target.getPlayer()),
                                            this.translator.getField(targetRecord.getLanguage(), "commons_punish_ban")
                                                    + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase(),
                                            -1,
                                            false,
                                            silent
                                    );
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
                                    this.punishmentHandler.createPunishment(
                                            PunishmentType.BAN,
                                            user.id(),
                                            targetRecord.id(),
                                            serverName,
                                            null,
                                            getFormattedIp(target.getPlayer()),
                                            banReason,
                                            expirationDate,
                                            false,
                                            silent
                                    );
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
                                this.punishmentHandler.createPunishment(
                                        PunishmentType.BAN,
                                        user.id(),
                                        targetRecord.id(),
                                        serverName,
                                        null,
                                        getFormattedIp(target.getPlayer()),
                                        banReason,
                                        -1,
                                        false,
                                        silent
                                );
                            } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
                                ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_punish_error") + ".");
                            }
                        } else {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_punish_error") + ".");
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
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.instance.playerIdentifier.get(player.getUniqueId())), userAsyncResponse -> {
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
                    CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.instance.playerIdentifier.get(target.getUniqueId())), targetAsyncResponse -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();
                            String serverName = "test"; //TODO: Get server from Cloud API

                            // Detecting if player is online
                            if (this.onlinePlayers.isPlayerOnline(targetRecord.id())) {
                                ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(), "commons_punish_offline".replace("%%url%%",
                                                ChatColor.YELLOW + "https://www.seocraft.net" + ChatColor.RED + "."
                                        )));
                                return;
                            }

                            // Check if user has lower priority
                            if (user.getPrimaryGroup().getPriority() > targetRecord.getPrimaryGroup().getPriority()) {
                                alertLowerPermissions(player, user.getLanguage());
                                return;
                            }

                            String reason = this.translator.getField(targetRecord.getLanguage(), "commons_punish_kick")
                                    + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase();
                            if (context.getArgumentsLength() > 1) reason = context.getJoinedArgs(1);

                            try {
                                this.punishmentHandler.createPunishment(
                                        PunishmentType.KICK,
                                        user.id(),
                                        targetRecord.id(),
                                        serverName,
                                        null,
                                        getFormattedIp(target.getPlayer()),
                                        reason,
                                        -1,
                                        false,
                                        silent
                                );
                            } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
                                ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_punish_error") + ".");
                            }
                        } else {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_punish_error") + ".");
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
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.instance.playerIdentifier.get(player.getUniqueId())), userAsyncResponse -> {
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
                    CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.instance.playerIdentifier.get(target.getUniqueId())), targetAsyncResponse -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();
                            String serverName = "test"; //TODO: Get server from Cloud API

                            // Detecting if player is online
                            if (this.onlinePlayers.isPlayerOnline(targetRecord.id())) {
                                ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(), "commons_punish_offline".replace("%%url%%",
                                                ChatColor.YELLOW + "https://www.seocraft.net" + ChatColor.RED + "."
                                        )));
                                return;
                            }

                            // Check if user has lower priority
                            if (user.getPrimaryGroup().getPriority() > targetRecord.getPrimaryGroup().getPriority()) {
                                alertLowerPermissions(player, user.getLanguage());
                                return;
                            }

                            String reason = this.translator.getField(targetRecord.getLanguage(), "commons_punish_warn")
                                    + this.translator.getUnspacedField(targetRecord.getLanguage(), "commons_punish_no_reason").toLowerCase();
                            if (context.getArgumentsLength() > 1) reason = context.getJoinedArgs(1);

                            try {
                                this.punishmentHandler.createPunishment(
                                        PunishmentType.WARN,
                                        user.id(),
                                        targetRecord.id(),
                                        serverName,
                                        null,
                                        getFormattedIp(target.getPlayer()),
                                        reason,
                                        -1,
                                        false,
                                        silent
                                );
                            } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
                                ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_punish_error") + ".");
                            }
                        } else {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_punish_error") + ".");
                        }
                    });

                } else {
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            });
        }
        return true;
    }

    private void alertLowerPermissions(Player player, String language) {
        ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                language,
                "commons_punish_lower_permissions")  + ".");
    }

    private String getFormattedIp(Player player) {
        return player.getAddress()
                .toString()
                .split(":")[0]
                .replace("/", "");
    }
}
