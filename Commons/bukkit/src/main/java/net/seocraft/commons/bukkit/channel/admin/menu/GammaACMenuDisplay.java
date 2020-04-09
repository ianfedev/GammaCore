package net.seocraft.commons.bukkit.channel.admin.menu;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.channel.admin.menu.ACMenuDisplay;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.InventoryUtils;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class GammaACMenuDisplay implements ACMenuDisplay {

    @Inject private TranslatableField translatableField;
    @Inject private UserStorageProvider userStorageProvider;

    @Override
    public void openInventory(@NotNull Player player) {
        CallbackWrapper.addCallback(userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), response -> {
            if (response.getStatus() == AsyncResponse.Status.SUCCESS) {

                Map<Integer, ItemStack> items = new HashMap<>();
                User user = response.getResponse();

                for (int i = 0; i < 54; i++)
                    if ((i < 10 || i > 43) || i == 17 || i == 18 || i == 26 || i == 27 || i == 35 || i == 36)
                        items.put(i, ACMenuIcons.getDivider());

                items.put(21, ACMenuIcons.getChatIcon(translatableField, user));
                items.put(30, ACMenuOptions.getChatOption(translatableField, user));

                items.put(22, ACMenuIcons.getLogsIcon(translatableField, user));
                items.put(31, ACMenuOptions.getLogsOption(translatableField, user));

                items.put(23, ACMenuIcons.getPunishmentIcon(translatableField, user));
                items.put(32, ACMenuOptions.getPunishmentOption(translatableField, user));

                items.put(40, ACMenuOptions.getCloseOption(translatableField, user));

                player.openInventory(
                        InventoryUtils.createInventory(this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_menu_title"), 54, items)
                );

            } else {
                ChatAlertLibrary.errorChatAlert(player);
                Bukkit.getLogger().log(Level.WARNING, "[Commons] Error opening admin settings inventory.", response.getThrowedException());
            }
        });
    }

}
