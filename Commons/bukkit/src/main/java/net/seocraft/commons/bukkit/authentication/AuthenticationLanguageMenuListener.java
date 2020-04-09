package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.InventoryUtils;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class AuthenticationLanguageMenuListener implements Listener {

    @Inject private TranslatableField translator;
    @Inject private UserStorageProvider userStorageProvider;

    @EventHandler
    public void authenticationLanguageMenuListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = player.getItemInHand();
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                Inventory languageSelector = InventoryUtils.createInventory(
                        this.translator.getUnspacedField(user.getLanguage(), "authentication_language_menu"),
                        9,
                        AuthenticationHeadHandler.getLanguageMenu()
                );
                player.openInventory(languageSelector);
            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }
        });
    }

}
