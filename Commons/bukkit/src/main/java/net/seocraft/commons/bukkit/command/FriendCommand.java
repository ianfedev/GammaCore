package net.seocraft.commons.bukkit.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Default;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.ChatGlyphs;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.friend.FriendshipAction;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.storage.Pagination;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.friend.FriendshipUserActions;
import net.seocraft.commons.core.model.GammaPagination;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Set;

@ACommand(names = {"friends", "friend", "f"})
public class FriendCommand implements CommandClass {

    @Inject
    private BukkitAPI bukkitAPI;
    @Inject
    private TranslatableField translatableField;
    @Inject
    private FriendshipProvider friendshipProvider;
    @Inject
    private FriendshipUserActions friendshipUserActions;
    @Inject
    private OnlineStatusManager onlineStatusManager;
    @Inject
    private UserFormatter userFormatter;
    @Inject
    private UserStorageProvider userStorageProvider;

    @ACommand(names = "")
    public boolean mainCommand(@Injected(true) CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
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

    @ACommand(names = "add")
    public boolean addCommand(@Injected(true) CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Check if target isn't same player
                    if (alertSamePlayer(player, user, target)) {
                        return;
                    }

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();

                            // Detect if users are already friends
                            if (alertFriendshipStatus(user, targetRecord, player)) {
                                return;
                            }

                            // Detect adding status or permission bypassing
                            if (!targetRecord.getGameSettings().getGeneral().isAcceptingFriends() && !player.hasPermission("commons.staff.friends.bypass")) {
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
        }
        return true;
    }

    @ACommand(names = "accept")
    public boolean acceptCommand(@Injected(true) CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Check if target isn't same player
                    if (alertSamePlayer(player, user, target)) {
                        return;
                    }

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
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
        }
        return true;
    }

    @ACommand(names = "reject")
    public boolean rejectCommand(@Injected(true) CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Check if target isn't same player
                    if (alertSamePlayer(player, user, target)) {
                        return;
                    }

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
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

    @ACommand(names = "remove")
    public boolean removeCommand(@Injected(true) CommandSender commandSender, OfflinePlayer target) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    //Check if target isn't same player
                    if (alertSamePlayer(player, user, target)) {
                        return;
                    }

                    // Obtain target player
                    CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
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
        }
        return true;
    }

    @ACommand(names = "removeall")
    public boolean removeAllCommand(@Injected(true) CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    try {
                        System.out.println("Erasing players");
                        this.friendshipProvider.removeAllFriends(user.getId());
                        System.out.println("Erased players");
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

    @ACommand(names = "force", permission = "commons.staff.friends.force")
    public boolean forceCommand(@Injected(true)  Player player, OfflinePlayer target, @Default OfflinePlayer second) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                //Check if target isn't same player
                if (alertSamePlayer(player, user, target)) {
                    return;
                }

                // Obtain first player
                CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                    if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User firstRecord = targetAsyncResponse.getResponse();

                        // Check if first player is online
                        if (!this.onlineStatusManager.isPlayerOnline(firstRecord.getId())) {
                            ChatAlertLibrary.errorChatAlert(player,
                                    this.translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_friends_force_offline"
                                    ) + "."
                            );
                            return;
                        }

                        // Force between sender and first if not first argument
                        if ((second != null && second.getName().equalsIgnoreCase(player.getName()))) {
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
                            CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(second.getName()), secondAsyncResponse -> {
                                if (secondAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                    User secondRecord = secondAsyncResponse.getResponse();

                                    if (
                                            user.getPrimaryGroup().getPriority() > firstRecord.getPrimaryGroup().getPriority() ||
                                                    user.getPrimaryGroup().getPriority() > secondRecord.getPrimaryGroup().getPriority()
                                    ) {
                                        ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_friends_force_lower_permissions") + ".");
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
        return true;
    }

    @ACommand(names = {"l", "list"})
    public boolean friendsList(@Injected(true) @Sender Player player, @Default("1") Integer page) {
        // Get base user
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {

                User user = userAsyncResponse.getResponse();
                try {
                    Set<User> playerList = this.friendshipProvider.listFriendsSync(user.getId());
                    if (playerList.size() > 0) {
                        Pagination<User> pagination = new GammaPagination<>(8, playerList);
                        player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                        // << Friends (Page X of X) >>
                        TextComponent leftArrow = new TextComponent("<<");
                        leftArrow.setColor(ChatColor.YELLOW);
                        leftArrow.setBold(true);
                        leftArrow.setHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder(
                                                this.translatableField.getUnspacedField(
                                                        user.getLanguage(),
                                                        "commons_pagination_click"
                                                ).replace("%%page%%", "" + (page - 1))
                                        ).color(ChatColor.YELLOW).create()
                                )
                        );
                        leftArrow.setClickEvent(
                                new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND,
                                        "/friends list " + (page - 1)
                                )
                        );

                        TextComponent actualPages = new TextComponent(" " +
                                this.translatableField.getField(
                                        user.getLanguage(),
                                        "commons_friends_word"
                                ) +
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_pagination_of"
                                ).replace(
                                        "%%first%%",
                                        "" + page
                                ).replace(
                                        "%%last%%",
                                        "" + pagination.totalPages()
                                ) + " "
                        );
                        actualPages.setColor(ChatColor.GOLD);

                        TextComponent rightArrow = new TextComponent(">>");
                        rightArrow.setColor(ChatColor.YELLOW);
                        rightArrow.setBold(true);
                        rightArrow.setHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder(
                                                this.translatableField.getUnspacedField(
                                                        user.getLanguage(),
                                                        "commons_pagination_click"
                                                ).replace("%%page%%", "" + (page + 1))
                                        ).color(ChatColor.YELLOW).create()
                                )
                        );
                        rightArrow.setClickEvent(
                                new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND,
                                        "/friends list " + (page + 1)
                                )
                        );

                        paginateType(player, page, user, pagination, leftArrow, actualPages, rightArrow);
                    } else {
                        player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                        player.sendMessage(ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_no_friends"));
                        player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                    }
                } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException unauthorized) {
                    ChatAlertLibrary.errorChatAlert(
                            player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_error")
                    );
                }
            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }
        });

        return true;
    }

    private void paginateType(Player player, int page, User user, Pagination<User> pagination, TextComponent leftArrow, TextComponent actualPages, TextComponent rightArrow) {
        TextComponent finalComponent = new TextComponent("");
        if (page != 1 && pagination.totalPages() != 1) finalComponent.addExtra(leftArrow);

        finalComponent.addExtra(actualPages);
        if (page != pagination.totalPages()) finalComponent.addExtra(rightArrow);

        player.spigot().sendMessage(finalComponent);

        pagination.getPage(1).forEach(friend -> {
            ChatColor color = ChatColor.RED;
            String field = "commons_friends_was";
            if (this.onlineStatusManager.isPlayerOnline(friend.getId())) {
                field = "commons_friends_in";
                color = ChatColor.YELLOW;
            }
            player.sendMessage(
                    this.userFormatter.getUserFormat(friend, this.bukkitAPI.getConfig().getString("realm")) + " " + color +
                            this.translatableField.getField(user.getLanguage(), field).toLowerCase() + user.getSessionInfo().getLastGame()
            );
        });
        player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
    }

    @ACommand(names = "requests")
    public boolean friendsRequests(@Injected(true) @Sender Player player, @Default("1") Integer page) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {

                User user = userAsyncResponse.getResponse();
                Set<User> playerList = this.friendshipProvider.getRequestsSync(user.getId());
                if (playerList.size() > 0) {
                    Pagination<User> pagination = new GammaPagination<>(8, playerList);
                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                    // << Requests (Page X of X) >>
                    TextComponent leftArrow = new TextComponent("<<");
                    leftArrow.setColor(ChatColor.YELLOW);
                    leftArrow.setBold(true);
                    leftArrow.setHoverEvent(
                            new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_pagination_click"
                                            ).replace("%%page%%", "" + (page - 1))
                                    ).color(ChatColor.YELLOW).create()
                            )
                    );
                    leftArrow.setClickEvent(
                            new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/friends requests " + (page - 1)
                            )
                    );

                    TextComponent actualPages = new TextComponent(" " +
                            this.translatableField.getField(
                                    user.getLanguage(),
                                    "commons_friends_requests"
                            ) +
                            this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_pagination_of"
                            ).replace(
                                    "%%first%%",
                                    "" + page
                            ).replace(
                                    "%%last%%",
                                    "" + pagination.totalPages()
                            ) + " "
                    );
                    actualPages.setColor(ChatColor.GOLD);

                    TextComponent rightArrow = new TextComponent(">>");
                    rightArrow.setColor(ChatColor.YELLOW);
                    rightArrow.setBold(true);
                    rightArrow.setHoverEvent(
                            new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_pagination_click"
                                            ).replace("%%page%%", "" + (page + 1))
                                    ).color(ChatColor.YELLOW).create()
                            )
                    );
                    rightArrow.setClickEvent(
                            new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/friends requests " + (page + 1)
                            )
                    );

                    paginateType(player, page, user, pagination, leftArrow, actualPages, rightArrow);
                } else {
                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                    player.sendMessage(ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_no_friends"));
                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                }
            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }
        });
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
                    "commons_friends_force_lower_permissions") + ".");
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
                            "commons_friends_not_yourself"
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