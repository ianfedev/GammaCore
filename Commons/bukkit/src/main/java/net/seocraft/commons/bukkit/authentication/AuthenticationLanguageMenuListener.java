package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.bukkit.user.UserStore;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.utils.InventoryUtils;
import net.seocraft.commons.core.translations.TranslatableField;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class AuthenticationLanguageMenuListener implements Listener {

    @Inject private TranslatableField translator;
    @Inject private UserStore userStorage;

    @EventHandler
    public void authenticationLanguageMenuListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = player.getItemInHand();
        if (NBTTagHandler.hasString(handItem, "accessor") &&
                NBTTagHandler.getString(handItem, "accessor").equalsIgnoreCase("language")) {
            CallbackWrapper.addCallback(this.userStorage.getUserObject(player.getName()), user -> {
                Inventory languageSelector = InventoryUtils.createInventory(
                        this.translator.getUnspacedField(user.getLanguage(), "authentication_language_menu"),
                        9,
                        AuthenticationHeadHandler.getLanguageMenu()
                );
                player.openInventory(languageSelector);
            });
        }
    }
}
