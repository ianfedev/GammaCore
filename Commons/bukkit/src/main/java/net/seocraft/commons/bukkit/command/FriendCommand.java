package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.ggamer55.bcm.parametric.CommandClass;
import me.ggamer55.bcm.parametric.annotation.Command;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserChat;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.friend.FriendshipHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.bukkit.util.ChatGlyphs;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommand implements CommandClass {

    @Inject private BukkitAPI bukkitAPI;
    @Inject private TranslatableField translatableField;
    @Inject private FriendshipHandler friendshipHandler;
    @Inject private UserChat userChatHandler;
    @Inject private CommonsBukkit instance;
    @Inject private UserStoreHandler userStoreHandler;
    @Inject private SessionHandler sessionHandler;

    @Command(names = {"friends", "friends help"})
    public boolean mainCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();
                    String l = user.getLanguage();

                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                    player.sendMessage(ChatColor.GOLD + this.translatableField.getUnspacedField(l, "commons_friends_title") + ":");
                    player.sendMessage(ChatColor.YELLOW + "/friends help" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help"));
                    player.sendMessage(ChatColor.YELLOW + "/friends add" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_add"));
                    player.sendMessage(ChatColor.YELLOW + "/friends accept" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_accept"));
                    player.sendMessage(ChatColor.YELLOW + "/friends deny" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_deny"));
                    player.sendMessage(ChatColor.YELLOW + "/friends list" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_list"));
                    player.sendMessage(ChatColor.YELLOW + "/friends remove" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_remove"));
                    player.sendMessage(ChatColor.YELLOW + "/friends requests" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_requests"));
                    player.sendMessage(ChatColor.YELLOW + "/friends removeall" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_removeall"));
                    if (player.hasPermission("commons.staff.friends.force"))
                    player.sendMessage(ChatColor.RED + "/friends force" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_force"));

                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                } else {
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            });
        }
        return true;
    }

    @Command(names = {"friends add"}, min = 1, usage = "/<command> <target>")
    public boolean addCommand(CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse  -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();

                            // Detect adding status or permission bypassing
                            if (!targetRecord.isAcceptingFriends() && !player.hasPermission("commons.staff.friends.bypass")) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_disabled_requests"));
                                return;
                            }

                            // Detect if users are already friends
                            if (this.friendshipHandler.checkFriendshipStatus(user.id(), targetRecord.id())) {
                                ChatAlertLibrary.errorChatAlert(player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_friends_already"
                                        ).replace(
                                                "%%player%%",
                                                this.userChatHandler.getUserFormat(
                                                        targetRecord,
                                                        this.bukkitAPI.getConfig().getString("realm")
                                                )
                                        )
                                );
                                return;
                            }





                        } else {
                            if (targetAsyncResponse.getThrowedException().getClass().equals(NotFound.class)) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_not_found"));
                            } else {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error"));
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
}
