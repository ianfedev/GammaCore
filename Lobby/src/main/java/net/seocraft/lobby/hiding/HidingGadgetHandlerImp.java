package net.seocraft.lobby.hiding;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.friend.FriendshipHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import net.seocraft.lobby.management.CooldownManager;
import net.seocraft.lobby.menu.HotbarItemCollection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HidingGadgetHandlerImp implements HidingGadgetHandler {

    @Inject private SessionHandler sessionHandler;
    @Inject private UserStoreHandler userStoreHandler;
    @Inject private HotbarItemCollection hotbarItemCollection;
    @Inject private TranslatableField translatableField;
    @Inject private CooldownManager cooldownManager;
    @Inject private FriendshipHandler friendshipHandler;

    @Override
    public void enableHiding(@NotNull Player player) {
        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.sessionHandler.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (checkCooldownStatus(user.getLanguage(), user.id(), player)) return;
                user.setHiding(true);
                try {
                    this.userStoreHandler.updateUser(user);
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer ->  {
                        GameSession handler = this.sessionHandler.getCachedSession(onlinePlayer.getName());
                        if (
                                !this.friendshipHandler.checkFriendshipStatus(user.id(), handler.getPlayerId()) &&
                                        !onlinePlayer.hasPermission("commons.staff.vanish")
                        ) {
                            player.hidePlayer(onlinePlayer);
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_lobby_hiding_hidden"
                                    )
                            );
                            this.cooldownManager.createCooldown(
                                    user.id(),
                                    "hidingGadget",
                                    3
                            );
                            setPlayerInventory(player, user);
                        }
                    });
                } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
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
    }

    @Override
    public void disableHiding(@NotNull Player player) {
        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.sessionHandler.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (checkCooldownStatus(user.getLanguage(), user.id(), player)) return;
                user.setHiding(false);
                try {
                    this.userStoreHandler.updateUser(user);
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer ->  {
                        player.showPlayer(onlinePlayer);
                        ChatAlertLibrary.infoAlert(
                                player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_lobby_hiding_unhidden"
                                )
                        );
                        this.cooldownManager.createCooldown(
                                user.id(),
                                "hidingGadget",
                                3
                        );
                        setPlayerInventory(player, user);
                    });
                } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
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
                    )
            );
            return true;
        }
        return false;
    }

}