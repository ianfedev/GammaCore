package net.seocraft.lobby.hiding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.lobby.HidingGadgetManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.cooldown.CooldownManager;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.Lobby;
import net.seocraft.lobby.hotbar.HotbarItemCollection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LobbyHidingGadget implements HidingGadgetManager {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private Lobby instance;
    @Inject private HotbarItemCollection hotbarItemCollection;
    @Inject private TranslatableField translatableField;
    @Inject private CooldownManager cooldownManager;
    @Inject private FriendshipProvider friendshipProvider;

    @Override
    public void enableHiding(@NotNull Player player) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (checkCooldownStatus(user.getLanguage(), user.getId(), player)) return;
                user.getGameSettings().getGeneral().setHidingPlayers(true);
                try {
                    this.userStorageProvider.updateUser(user);
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer ->  {
                        if (
                                !this.friendshipProvider.checkFriendshipStatus(user.getId(), onlinePlayer.getDatabaseIdentifier()) &&
                                        !onlinePlayer.hasPermission("commons.staff.vanish") &&
                                        player != onlinePlayer
                        ) {
                            Bukkit.getScheduler().runTask(this.instance, () -> {
                                player.hidePlayer(onlinePlayer);
                            });
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
    }

    @Override
    public void disableHiding(@NotNull Player player) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (checkCooldownStatus(user.getLanguage(), user.getId(), player)) return;
                user.getGameSettings().getGeneral().setHidingPlayers(false);
                try {
                    this.userStorageProvider.updateUser(user);
                    Bukkit.getOnlinePlayers().forEach(playerQuery -> {
                        Bukkit.getScheduler().runTask(this.instance, () -> {
                            player.showPlayer(playerQuery);
                        });
                    });
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
    }

    private void setPlayerInventory(Player player, User user) {
        player.getInventory().setItem(
                1,
                this.hotbarItemCollection.getHidingGadget(
                        user.getLanguage(),
                        user.getGameSettings().getGeneral().isHidingPlayers()
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
