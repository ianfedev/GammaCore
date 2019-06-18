package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.ggamer55.bcm.CommandContext;
import me.ggamer55.bcm.parametric.CommandClass;
import me.ggamer55.bcm.parametric.annotation.Command;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserChat;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.online.OnlinePlayersApi;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.friend.FriendshipAction;
import net.seocraft.commons.bukkit.friend.FriendshipHandler;
import net.seocraft.commons.bukkit.friend.FriendshipUserActions;
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
    @Inject private FriendshipUserActions friendshipUserActions;
    @Inject private OnlinePlayersApi onlinePlayersApi;
    @Inject private UserChat userChatHandler;
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
                    player.sendMessage(ChatColor.YELLOW + "/friends reject" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_deny"));
                    player.sendMessage(ChatColor.YELLOW + "/friends list" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_list"));
                    player.sendMessage(ChatColor.YELLOW + "/friends remove" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_remove"));
                    player.sendMessage(ChatColor.YELLOW + "/friends status" + ChatColor.GRAY + " - " + ChatColor.AQUA + this.translatableField.getUnspacedField(l, "commons_friends_help_status"));
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

                    //Check if target isn't same player
                    if (player.getName().equalsIgnoreCase(target.getName())) {
                        ChatAlertLibrary.errorChatAlert(player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_friends_not_add_yourself"
                                )
                        );
                        return;
                    }

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse  -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();

                            // Detect adding status or permission bypassing
                            if (!targetRecord.isAcceptingFriends() && !player.hasPermission("commons.staff.friends.bypass")) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_disabled_requests") + ".");
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
                                                ) + ChatColor.RED
                                        ) + "."
                                );
                                return;
                            }

                            if (this.friendshipHandler.requestIsSent(user.id(), targetRecord.id())) {
                                ChatAlertLibrary.errorChatAlert(player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_friends_already_requested"
                                        ).replace(
                                                "%%player%%",
                                                this.userChatHandler.getUserFormat(
                                                        targetRecord,
                                                        this.bukkitAPI.getConfig().getString("realm")
                                                ) + ChatColor.RED
                                        ) + "."
                                );
                                return;
                            }

                            this.friendshipHandler.createFriendRequest(
                                    user.id(),
                                    targetRecord.id()
                            );

                            this.friendshipUserActions.senderAction(player, user, targetRecord, FriendshipAction.CREATE);
                            this.friendshipUserActions.receiverAction(user, targetRecord, FriendshipAction.CREATE, null);
                        } else {
                            if (targetAsyncResponse.getThrowedException().getClass().equals(NotFound.class)) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_not_found") + ".");
                            } else {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
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

    @Command(names = {"friends accept"}, min = 1, usage = "/<command> <target>")
    public boolean acceptCommand(CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Check if target isn't same player
                    if (player.getName().equalsIgnoreCase(target.getName())) {
                        ChatAlertLibrary.errorChatAlert(player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_friends_not_add_yourself"
                                )
                        );
                        return;
                    }

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse  -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();

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
                                                ) + ChatColor.RED
                                        ) + "."
                                );
                                return;
                            }

                            // Detect if target sent friendship request
                            if (!this.friendshipHandler.requestIsSent(targetRecord.id(), user.id())) {
                                ChatAlertLibrary.errorChatAlert(player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_friends_not_requested"
                                        ).replace(
                                                "%%player%%",
                                                this.userChatHandler.getUserFormat(
                                                        targetRecord,
                                                        this.bukkitAPI.getConfig().getString("realm")
                                                ) + ChatColor.RED
                                        ) + "."
                                );
                                return;
                            }

                            try {
                                this.friendshipHandler.acceptFriendRequest(
                                        user.id(),
                                        targetRecord.id()
                                );
                            } catch (Unauthorized | BadRequest | InternalServerError | NotFound unauthorized) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
                                return;
                            }

                            this.friendshipUserActions.senderAction(player, user, targetRecord, FriendshipAction.ACCEPT);
                            this.friendshipUserActions.receiverAction(user, targetRecord, FriendshipAction.ACCEPT, null);
                        } else {
                            if (targetAsyncResponse.getThrowedException().getClass().equals(NotFound.class)) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_not_found") + ".");
                            } else {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
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

    @Command(names = {"friends reject"}, min = 1, usage = "/<command> <target>")
    public boolean rejectCommand(CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Check if target isn't same player
                    if (player.getName().equalsIgnoreCase(target.getName())) {
                        ChatAlertLibrary.errorChatAlert(player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_friends_not_remove_yourself"
                                ) + "."
                        );
                        return;
                    }

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse  -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();

                            // Detect if target sent friendship request
                            if (!this.friendshipHandler.requestIsSent(targetRecord.id(), user.id())) {
                                ChatAlertLibrary.errorChatAlert(player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_friends_not_requested"
                                        ).replace(
                                                "%%player%%",
                                                this.userChatHandler.getUserFormat(
                                                        targetRecord,
                                                        this.bukkitAPI.getConfig().getString("realm")
                                                ) + ChatColor.RED
                                        ) + "."
                                );
                                return;
                            }

                            this.friendshipHandler.rejectFriendRequest(user.id(), targetRecord.id());

                            player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                            player.sendMessage(
                                    ChatColor.RED +
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_friends_request_rejected"
                                            ).replace(
                                                    "%%player%%",
                                                    this.userChatHandler.getUserFormat(
                                                            targetRecord,
                                                            this.bukkitAPI.getConfig().getString("realm")
                                                    ) + ChatColor.RED
                                            )
                            );
                            player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                        } else {
                            if (targetAsyncResponse.getThrowedException().getClass().equals(NotFound.class)) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_not_found") + ".");
                            } else {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
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

    @Command(names = {"friends remove"}, min = 1, usage = "/<command> <target>")
    public boolean removeCommand(CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Check if target isn't same player
                    if (player.getName().equalsIgnoreCase(target.getName())) {
                        ChatAlertLibrary.errorChatAlert(player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_friends_not_remove_yourself"
                                ) + "."
                        );
                        return;
                    }

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse  -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();

                            // Detect if users are friends
                            if (!this.friendshipHandler.checkFriendshipStatus(user.id(), targetRecord.id())) {
                                ChatAlertLibrary.errorChatAlert(player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_friends_not_friends"
                                        ).replace(
                                                "%%player%%",
                                                this.userChatHandler.getUserFormat(
                                                        targetRecord,
                                                        this.bukkitAPI.getConfig().getString("realm")
                                                ) + ChatColor.RED
                                        ) + "."
                                );
                                return;
                            }

                            try {
                                this.friendshipHandler.removeFriend(user.id(), targetRecord.id());
                            } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_not_found") + ".");
                                return;
                            }

                            player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                            player.sendMessage(
                                    ChatColor.RED +
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_friends_removed"
                                            ).replace(
                                                    "%%player%%",
                                                    this.userChatHandler.getUserFormat(
                                                            targetRecord,
                                                            this.bukkitAPI.getConfig().getString("realm")
                                                    ) + ChatColor.RED
                                            ) + "."
                            );
                            player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                        } else {
                            if (targetAsyncResponse.getThrowedException().getClass().equals(NotFound.class)) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_not_found") + ".");
                            } else {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
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

    @Command(names = {"friends removeall"})
    public boolean removeAllCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    try {
                        this.friendshipHandler.removeAllFriends(user.id());
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
                        ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
                        return;
                    }

                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                    player.sendMessage(
                            ChatColor.YELLOW +
                                    this.translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_friends_removed_all"
                                    )
                    );
                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                } else {
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            });
        }
        return true;
    }

    @Command(names = {"friends force"}, min = 1, usage = "/<command> <target>", permission = "commons.staff.friends.force")
    public boolean forceCommand(CommandSender commandSender, CommandContext context, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());

            // Get base user
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Check if target isn't same player
                    if (player.getName().equalsIgnoreCase(target.getName())) {
                        ChatAlertLibrary.errorChatAlert(player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_friends_not_remove_yourself"
                                ) + "."
                        );
                        return;
                    }

                    // Obtain first player
                    CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse  -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User firstRecord = targetAsyncResponse.getResponse();

                            // Check if first player is online
                            if (!this.onlinePlayersApi.isPlayerOnline(firstRecord.id())) {
                                ChatAlertLibrary.errorChatAlert(player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_friends_force_offline"
                                        ) + "."
                                );
                                return;
                            }

                            // Force between sender and first if not first argument
                            if (context.getArgumentsLength() == 1) {
                                // Check if player has higher permissions
                                if (hasLowerPermissions(user, firstRecord, player)) return;

                                //TODO: Send message to sender
                                this.friendshipUserActions.receiverAction(user, firstRecord, FriendshipAction.FORCE, user);
                            }

                        } else {
                            if (targetAsyncResponse.getThrowedException().getClass().equals(NotFound.class)) {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_not_found") + ".");
                            } else {
                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
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
            ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(
                    user.getLanguage(),
                    "commons_friends_force_lower_permissions")  + ".");
            return true;
        } else {
            return false;
        }
    }
}