package net.seocraft.commons.bukkit.channel.admin.menu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.channel.admin.menu.ACMenuDisplay;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

public class ACClickInventoryListener implements Listener {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;
    @Inject private ACMenuDisplay acMenuDisplay;

    @EventHandler
    public void playerClickInventoryEvent(InventoryClickEvent event) {
        if (event.isLeftClick()) {
            Player player = (Player) event.getWhoClicked();

            try {
                User user = userStorageProvider.getCachedUserSync(player.getDatabaseIdentifier());
                ItemStack clicked = event.getCurrentItem();
                if (clicked != null && NBTTagHandler.hasString(clicked, "ac_selector")) {
                    switch (NBTTagHandler.getString(clicked, "ac_selector")) {
                        case "chat": {
                            user.getGameSettings().getAdminChat().setActive(!user.getGameSettings().getAdminChat().isActive());
                            updateUser(user, player);
                            break;
                        }
                        case "logs": {
                            user.getGameSettings().getAdminChat().setActiveLogs(!user.getGameSettings().getAdminChat().hasActiveLogs());
                            updateUser(user, player);
                            break;
                        }
                        case "punishment": {
                            user.getGameSettings().getAdminChat().setActivePunishments(!user.getGameSettings().getAdminChat().hasActivePunishments());
                            updateUser(user, player);
                            break;
                        }
                        default: {
                            player.closeInventory();
                            break;
                        }
                    }
                    event.setCancelled(true);
                }
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "[Commons] Error updating player admin stats.", e);
                ChatAlertLibrary.errorChatAlert(player);
                player.closeInventory();
            }
        }
    }

    private void updateUser(@NotNull User user, @NotNull Player player) throws Unauthorized, JsonProcessingException, BadRequest, NotFound, InternalServerError {
        player.closeInventory();
        this.userStorageProvider.updateUser(user);
        acMenuDisplay.openInventory(player);
        ChatAlertLibrary.infoAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_menu_updated"));

    }
}
