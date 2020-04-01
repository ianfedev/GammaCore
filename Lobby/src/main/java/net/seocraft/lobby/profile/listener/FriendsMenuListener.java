package net.seocraft.lobby.profile.listener;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.anvil.AnvilGUI;
import net.seocraft.api.bukkit.profile.ProfileManager;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.bukkit.utils.ChatGlyphs;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.friend.FriendshipAction;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.friend.FriendshipUserActions;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.logging.Level;

public class FriendsMenuListener implements Listener {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private UserFormatter userFormatter;
    @Inject private FriendshipProvider friendshipProvider;
    @Inject private Plugin plugin;
    @Inject private FriendshipUserActions friendshipUserActions;
    @Inject private TranslatableField translatableField;
    @Inject private ProfileManager profileManager;

    @EventHandler
    public void profileMenuListener(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && (NBTTagHandler.hasString(clickedItem, "friends_accessor") || NBTTagHandler.hasString(clickedItem, "friends_page"))) {
            if (event.getClick() == ClickType.LEFT) {
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();

                        if (NBTTagHandler.hasString(clickedItem, "friends_page")) {
                            int page = 1;
                            try {
                                page = Integer.parseInt(NBTTagHandler.getString(clickedItem, "friends_page"));
                            } catch (NumberFormatException ignore) {}
                            this.profileManager.openFriendsMenu(user, page);
                        }

                        switch (NBTTagHandler.getString(clickedItem, "friends_accessor")) {
                            case "add": {
                                new AnvilGUI.Builder()
                                        .onClose((p) -> this.profileManager.openFriendsMenu(user))
                                        .onComplete((p, t) -> {
                                            try {
                                                User target = this.userStorageProvider.findUserByNameSync(t);

                                                if (this.friendshipProvider.checkFriendshipStatus(user.getId(), target.getId()))
                                                    return AnvilGUI.Response.text(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_friends_already"));

                                                this.friendshipProvider.createFriendRequest(user.getId(), target.getId());
                                                this.friendshipUserActions.senderAction(player, user, target, FriendshipAction.CREATE, null);
                                                this.friendshipUserActions.receiverAction(user, target, FriendshipAction.CREATE, null);
                                            } catch (Unauthorized | BadRequest | InternalServerError | IOException e) {
                                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_transaction_error"));
                                                Bukkit.getLogger().log(Level.WARNING, "[Lobby] There was an error with adding user.", e);
                                            } catch (NotFound notFound) {
                                                return AnvilGUI.Response.text(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_friends_not_found"));
                                            }
                                            return AnvilGUI.Response.close();
                                        })
                                        .text(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_friends_add"))
                                        .plugin(plugin)
                                        .open(player);
                                break;
                            }
                            case "remove": {
                                new AnvilGUI.Builder()
                                        .onClose((p) -> this.profileManager.openFriendsMenu(user))
                                        .onComplete((p, t) -> {
                                            try {
                                                User target = this.userStorageProvider.findUserByNameSync(t);

                                                if (!this.friendshipProvider.checkFriendshipStatus(user.getId(), target.getId()))
                                                    return AnvilGUI.Response.text(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_friends_relation"));

                                                this.friendshipProvider.removeFriend(user.getId(), target.getId());

                                                player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                                                player.sendMessage(
                                                        ChatColor.RED +
                                                                this.translatableField.getUnspacedField(
                                                                        user.getLanguage(),
                                                                        "commons_friends_removed"
                                                                ).replace(
                                                                        "%%player%%",
                                                                        this.userFormatter.getUserFormat(
                                                                                target,
                                                                                this.bukkitAPI.getConfig().getString("realm")
                                                                        ) + ChatColor.RED
                                                                ) + "."
                                                );
                                                player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                                            } catch (Unauthorized | BadRequest | InternalServerError | IOException e) {
                                                ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_transaction_error"));
                                                Bukkit.getLogger().log(Level.WARNING, "[Lobby] There was an error with removing user.", e);
                                            } catch (NotFound notFound) {
                                                return AnvilGUI.Response.text(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_friends_not_found"));
                                            }
                                            return AnvilGUI.Response.close();
                                        })
                                        .text(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_friends_add"))
                                        .plugin(plugin)
                                        .open(player);
                                break;
                            }
                        }
                    } else {
                        ChatAlertLibrary.errorChatAlert(player);
                    }
                });
            }
            event.setCancelled(true);
        }
    }

}
