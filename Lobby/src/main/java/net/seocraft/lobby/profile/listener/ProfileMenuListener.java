package net.seocraft.lobby.profile.listener;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.profile.ProfileManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ProfileMenuListener implements Listener {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private ProfileManager profileManager;

    @EventHandler
    public void profileMenuListener(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null &&
                (NBTTagHandler.hasString(clickedItem, "lobby_accessor") || NBTTagHandler.hasString(clickedItem, "lobby_accessor"))) {
            if (event.getClick() == ClickType.LEFT) {
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();
                        switch (NBTTagHandler.getString(clickedItem, "lobby_accessor")) {
                            case "friends": {
                                this.profileManager.openFriendsMenu(user);
                                break;
                            }
                            case "language": {
                                this.profileManager.openLanguageMenu(user);
                                break;
                            }
                            case "social": {
                                this.profileManager.openSocialMenu(user);
                                break;
                            }
                            default:
                                break;
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
