package net.seocraft.lobby.profile.listener;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.profile.ProfileManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
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
        if (clickedItem != null && NBTTagHandler.hasString(clickedItem, "lobby_accessor")) {
            System.out.println("Clicked item not null");
            if (event.getClick() == ClickType.LEFT) {
                System.out.println("Click left");
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        System.out.println("Success user");
                        System.out.println(NBTTagHandler.getString(clickedItem, "lobby_accessor"));
                        User user = userAsyncResponse.getResponse();
                        switch (NBTTagHandler.getString(clickedItem, "lobby_accessor")) {
                            case "friends": {
                                this.profileManager.openFriendsMenu(user);
                                break;
                            }
                            case "social": {
                                int i = 0;
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
