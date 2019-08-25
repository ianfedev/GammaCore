package net.seocraft.commons.bukkit.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.friend.FriendshipAction;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.commons.bukkit.friend.FriendshipUserActions;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.bukkit.util.ChatGlyphs;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class FriendCommand implements CommandClass {

    @Inject private BukkitAPI bukkitAPI;
    @Inject private TranslatableField translatableField;
    @Inject private FriendshipProvider friendshipProvider;
    @Inject private FriendshipUserActions friendshipUserActions;
    @Inject private UserFormatter userFormatter;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private GameSessionManager gameSessionManager;

    @Command(names = {"friends", "friends help"})
    public boolean mainCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                if (playerSession != null) {
                    CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
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
                } else {
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        }
        return true;
    }

    @Command(names = {"friends add"}, min = 1, usage = "/<command> <target>")
    public boolean addCommand(CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = null;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();

                        //Check if target isn't same player
                        if (alertSamePlayer(player, user, target)) {
                            return;
                        }

                        // Obtain target player
                        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse  -> {
                            if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                User targetRecord = targetAsyncResponse.getResponse();

                                // Detect if users are already friends
                                if (alertFriendshipStatus(user, targetRecord, player)) {
                                    return;
                                }

                                // Detect adding status or permission bypassing
                                if (!targetRecord.isAcceptingFriends() && !player.hasPermission("commons.staff.friends.bypass")) {
                                    ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_disabled_requests") + ".");
                                    return;
                                }

                                if (this.friendshipProvider.requestIsSent(user.getId(), targetRecord.getId())) {
                                    ChatAlertLibrary.errorChatAlert(player,
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_friends_already_requested"
                                            ).replace(
                                                    "%%player%%",
                                                    this.userFormatter.getUserFormat(
                                                            targetRecord,
                                                            this.bukkitAPI.getConfig().getString("realm")
                                                    ) + ChatColor.RED
                                            ) + "."
                                    );
                                    return;
                                }

                                try {
                                    this.friendshipProvider.createFriendRequest(
                                            user.getId(),
                                            targetRecord.getId()
                                    );
                                } catch (JsonProcessingException e) {
                                    ChatAlertLibrary.errorChatAlert(player, null);
                                }

                                this.friendshipUserActions.senderAction(player, user, targetRecord, FriendshipAction.CREATE, null);
                                this.friendshipUserActions.receiverAction(user, targetRecord, FriendshipAction.CREATE, null);
                            } else {
                                sendNotFoundMessage(targetAsyncResponse.getThrowedException().getClass(), player, user);
                            }
                        });
                    } else {
                        ChatAlertLibrary.errorChatAlert(player, null);
                    }
                });
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player, null);
            }

        }
        return true;
    }

    @Command(names = {"friends accept"}, min = 1, usage = "/<command> <target>")
    public boolean acceptCommand(CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();

                        //Check if target isn't same player
                        if (alertSamePlayer(player, user, target)) {
                            return;
                        }

                        // Obtain target player
                        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse  -> {
                            if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                User targetRecord = targetAsyncResponse.getResponse();

                                // Detect if users are already friends
                                if (alertFriendshipStatus(user, targetRecord, player)) {
                                    return;
                                }

                                // Detect if target sent friendship request
                                if (alertIfNotRequested(player, user, targetRecord)) return;

                                try {
                                    this.friendshipProvider.acceptFriendRequest(
                                            user.getId(),
                                            targetRecord.getId()
                                    );
                                } catch (Unauthorized | BadRequest | InternalServerError | NotFound | JsonProcessingException unauthorized) {
                                    ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
                                    return;
                                }

                                this.friendshipUserActions.senderAction(player, user, targetRecord, FriendshipAction.ACCEPT, null);
                                this.friendshipUserActions.receiverAction(user, targetRecord, FriendshipAction.ACCEPT, null);
                            } else {
                                sendNotFoundMessage(targetAsyncResponse.getThrowedException().getClass(), player, user);
                            }
                        });
                    } else {
                        ChatAlertLibrary.errorChatAlert(player, null);
                    }
                });
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        }
        return true;
    }

    @Command(names = {"friends reject"}, min = 1, usage = "/<command> <target>")
    public boolean rejectCommand(CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = null;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();

                        //Check if target isn't same player
                        if (alertSamePlayer(player, user, target)) {
                            return;
                        }

                        // Obtain target player
                        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse  -> {
                            if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                User targetRecord = targetAsyncResponse.getResponse();

                                // Detect if target sent friendship request
                                if (alertIfNotRequested(player, user, targetRecord)) return;

                                this.friendshipProvider.rejectFriendRequest(user.getId(), targetRecord.getId());

                                player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                                player.sendMessage(
                                        ChatColor.RED +
                                                this.translatableField.getUnspacedField(
                                                        user.getLanguage(),
                                                        "commons_friends_request_rejected"
                                                ).replace(
                                                        "%%player%%",
                                                        this.userFormatter.getUserFormat(
                                                                targetRecord,
                                                                this.bukkitAPI.getConfig().getString("realm")
                                                        ) + ChatColor.RED
                                                )
                                );
                                player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                            } else {
                                sendNotFoundMessage(targetAsyncResponse.getThrowedException().getClass(), player, user);
                            }
                        });
                    } else {
                        ChatAlertLibrary.errorChatAlert(player, null);
                    }
                });
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        }
        return true;
    }

    private boolean alertIfNotRequested(Player player, User user, User targetRecord) {
        if (!this.friendshipProvider.requestIsSent(targetRecord.getId(), user.getId())) {
            ChatAlertLibrary.errorChatAlert(player,
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_friends_not_requested"
                    ).replace(
                            "%%player%%",
                            this.userFormatter.getUserFormat(
                                    targetRecord,
                                    this.bukkitAPI.getConfig().getString("realm")
                            ) + ChatColor.RED
                    ) + "."
            );
            return true;
        }
        return false;
    }

    @Command(names = {"friends remove"}, min = 1, usage = "/<command> <target>")
    public boolean removeCommand(CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = null;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();

                        //Check if target isn't same player
                        if (alertSamePlayer(player, user, target)) {
                            return;
                        }

                        // Obtain target player
                        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse  -> {
                            if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                User targetRecord = targetAsyncResponse.getResponse();

                                // Detect if users are friends
                                if (!this.friendshipProvider.checkFriendshipStatus(user.getId(), targetRecord.getId())) {
                                    ChatAlertLibrary.errorChatAlert(player,
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_friends_not_friends"
                                            ).replace(
                                                    "%%player%%",
                                                    this.userFormatter.getUserFormat(
                                                            targetRecord,
                                                            this.bukkitAPI.getConfig().getString("realm")
                                                    ) + ChatColor.RED
                                            ) + "."
                                    );
                                    return;
                                }

                                try {
                                    this.friendshipProvider.removeFriend(user.getId(), targetRecord.getId());
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
                                                        this.userFormatter.getUserFormat(
                                                                targetRecord,
                                                                this.bukkitAPI.getConfig().getString("realm")
                                                        ) + ChatColor.RED
                                                ) + "."
                                );
                                player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                            } else {
                                sendNotFoundMessage(targetAsyncResponse.getThrowedException().getClass(), player, user);
                            }
                        });
                    } else {
                        ChatAlertLibrary.errorChatAlert(player, null);
                    }
                });
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        }
        return true;
    }

    @Command(names = {"friends removeall"})
    public boolean removeAllCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();

                        try {
                            this.friendshipProvider.removeAllFriends(user.getId());
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
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        }
        return true;
    }

    @Command(names = {"friends force"}, min = 1, usage = "/<command> <target> [second]", permission = "commons.staff.friends.force")
    public boolean forceCommand(CommandSender commandSender, CommandContext context, OfflinePlayer target, OfflinePlayer second) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                // Get base user
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();

                        //Check if target isn't same player
                        if (alertSamePlayer(player, user, target)) {
                            return;
                        }

                        // Obtain first player
                        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse  -> {
                            if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                User firstRecord = targetAsyncResponse.getResponse();

                                // Check if first player is online
                                if (!gameSessionManager.sessionExists(firstRecord.getUsername())) {
                                    ChatAlertLibrary.errorChatAlert(player,
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_friends_force_offline"
                                            ) + "."
                                    );
                                    return;
                                }

                                // Force between sender and first if not first argument
                                if (context.getArgumentsLength() == 1 || (second != null && second.getName().equalsIgnoreCase(player.getName()))) {
                                    // Check if player has higher permissions
                                    if (hasLowerPermissions(user, firstRecord, player)) return;

                                    // Detect if players are already friends
                                    if (alertFriendshipStatus(firstRecord, user, player, true)) {
                                        return;
                                    }

                                    forcedActions(player, user, user, firstRecord);
                                    return;
                                }

                                if (second != null) {
                                    CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(second.getName()), secondAsyncResponse  -> {
                                        if (secondAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                            User secondRecord = secondAsyncResponse.getResponse();

                                            if (
                                                    user.getPrimaryGroup().getPriority() > firstRecord.getPrimaryGroup().getPriority() ||
                                                            user.getPrimaryGroup().getPriority() > secondRecord.getPrimaryGroup().getPriority()
                                            ) {
                                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(
                                                        user.getLanguage(),
                                                        "commons_friends_force_lower_permissions")  + ".");
                                                return;
                                            }

                                            // Detect if players are already friends
                                            if (alertFriendshipStatus(firstRecord, secondRecord, player, true)) {
                                                return;
                                            }

                                            forcedActions(player, user, firstRecord, secondRecord);
                                        } else {
                                            sendNotFoundMessage(targetAsyncResponse.getThrowedException().getClass(), player, user);
                                        }
                                    });
                                } else {
                                    ChatAlertLibrary.errorChatAlert(player);
                                }
                            } else {
                                sendNotFoundMessage(targetAsyncResponse.getThrowedException().getClass(), player, user);
                            }
                        });
                    } else {
                        ChatAlertLibrary.errorChatAlert(player);
                    }
                });
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        }
        return true;
    }

    private void forcedActions(Player player, User user, User firstRecord, User secondRecord) {

        sendRequest(player, user, firstRecord, secondRecord);

        this.friendshipUserActions.senderAction(player, firstRecord, secondRecord, FriendshipAction.FORCE, user);
        this.friendshipUserActions.receiverAction(firstRecord, secondRecord, FriendshipAction.FORCE, user);
    }

    private void sendRequest(Player player, User user, User firstRecord, User secondRecord) {
        try {
            this.friendshipProvider.forceFriend(
                    firstRecord.getId(),
                    secondRecord.getId(),
                    user.getId()
            );
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | JsonProcessingException unauthorized) {
            ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
        }
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

    private boolean alertFriendshipStatus(User user, User targetRecord, Player player, boolean forced) {
        String field = "commons_friends_already";
        if (forced) field = "commons_friends_already_forced";
        if (this.friendshipProvider.checkFriendshipStatus(user.getId(), targetRecord.getId())) {
            ChatAlertLibrary.errorChatAlert(player,
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            field
                    ).replace(
                            "%%player%%",
                            this.userFormatter.getUserFormat(
                                    targetRecord,
                                    this.bukkitAPI.getConfig().getString("realm")
                            ) + ChatColor.RED
                    ) + "."
            );
            return true;
        } else {
            return false;
        }
    }


    private boolean alertFriendshipStatus(User user, User targetRecord, Player player) {
        return this.alertFriendshipStatus(user, targetRecord, player, false);
    }

    private boolean alertSamePlayer(Player player, User user, OfflinePlayer target) {
        if (player.getName().equalsIgnoreCase(target.getName())) {
            ChatAlertLibrary.errorChatAlert(player,
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_friends_not_remove_yourself"
                    ) + "."
            );
            return true;
        } else {
            return false;
        }
    }

    private void sendNotFoundMessage(Class notFound, Player player, User user) {
        if (notFound.equals(NotFound.class)) {
            ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_not_found") + ".");
        } else {
            ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_system_error") + ".");
        }
    }
}