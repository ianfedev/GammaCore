package net.seocraft.lobby.hiding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.lobby.HidingGadgetManager;
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
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.api.core.cooldown.CooldownManager;
import net.seocraft.lobby.menu.HotbarItemCollection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LobbyHidingGadget implements HidingGadgetManager {

    @Inject private GameSessionManager gameSessionManager;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private HotbarItemCollection hotbarItemCollection;
    @Inject private TranslatableField translatableField;
    @Inject private CooldownManager cooldownManager;
    @Inject private FriendshipProvider friendshipProvider;

    @Override
    public void enableHiding(@NotNull Player player) {
        GameSession session = null;
        try {
            session = this.gameSessionManager.getCachedSession(player.getName());
            if (session != null) {
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(this.gameSessionManager.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();
                        if (checkCooldownStatus(user.getLanguage(), user.getId(), player)) return;
                        user.setHiding(true);
                        try {
                            this.userStorageProvider.updateUser(user);
                            Bukkit.getOnlinePlayers().forEach(onlinePlayer ->  {
                                GameSession handler = null;
                                try {
                                    handler = this.gameSessionManager.getCachedSession(onlinePlayer.getName());
                                    if (
                                            handler != null &&
                                                    !this.friendshipProvider.checkFriendshipStatus(user.getId(), handler.getPlayerId()) &&
                                                    !onlinePlayer.hasPermission("commons.staff.vanish") &&
                                                    player != onlinePlayer
                                    ) {
                                        player.hidePlayer(onlinePlayer);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_lobby_hiding_hidden"
                                    )
                            );
                            this.cooldownManager.createCooldown(
                                    user.getId(),
                                    "hidingGadget",
                                    3
                            );
                            setPlayerInventory(player, user);
                        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | JsonProcessingException exception) {
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_lobby_hiding_error"
                                    ) + "."
                            );
                        }
                    } else {
                        ChatAlertLibrary.errorChatAlert(player, null);
                    }
                });
            } else {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        } catch (IOException e) {
            ChatAlertLibrary.errorChatAlert(player);
        }
    }

    @Override
    public void disableHiding(@NotNull Player player) {
        GameSession session = null;
        try {
            session = this.gameSessionManager.getCachedSession(player.getName());
            if (session != null)  {
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(session.getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();
                        if (checkCooldownStatus(user.getLanguage(), user.getId(), player)) return;
                        user.setHiding(false);
                        try {
                            this.userStorageProvider.updateUser(user);
                            Bukkit.getOnlinePlayers().forEach(player::showPlayer);
                            ChatAlertLibrary.infoAlert(
                                    player,
                                    this.translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_lobby_hiding_unhidden"
                                    )
                            );
                            this.cooldownManager.createCooldown(
                                    user.getId(),
                                    "hidingGadget",
                                    3
                            );
                            setPlayerInventory(player, user);
                        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | JsonProcessingException exception) {
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_lobby_hiding_error"
                                    ) + "."
                            );
                        }
                    } else {
                        System.out.println("Nulled");
                        ChatAlertLibrary.errorChatAlert(player, null);
                    }
                });
            } else {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPlayerInventory(Player player, User user) {
        player.getInventory().setItem(
                1,
                this.hotbarItemCollection.getHidingGadget(
                        user.getLanguage(),
                        user.isHiding()
                )
        );
    }

    private boolean checkCooldownStatus(String l, String id, Player player) {
        if (this.cooldownManager.hasCooldown(id, "hidingGadget")) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    this.translatableField.getUnspacedField(
                            l,
                            "commons_cooldown_delay"
                    ) + "."
            );
            return true;
        }
        return false;
    }

}
