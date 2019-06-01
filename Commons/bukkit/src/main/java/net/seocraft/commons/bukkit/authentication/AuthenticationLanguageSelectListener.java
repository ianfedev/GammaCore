package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.bukkit.user.UserStore;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.serialization.JsonUtils;
import net.seocraft.api.shared.user.UserUpdateRequest;
import net.seocraft.commons.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class AuthenticationLanguageSelectListener implements Listener {

    @Inject private TranslatableField translator;
    @Inject private JsonUtils parser;
    @Inject private ServerTokenQuery tokenQuery;
    @Inject private UserUpdateRequest userUpdateRequest;
    @Inject private UserStore userStorage;

    @EventHandler(priority = EventPriority.LOWEST)
    public void authenticationLanguageSelectListener(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        HumanEntity entity = event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if ((entity instanceof Player)) {
            Player player = (Player) entity;
            if (event.getClick().equals(ClickType.LEFT) && NBTTagHandler.hasString(clickedItem, "language_accessor")) {
                CallbackWrapper.addCallback(this.userStorage.getUserObject(player.getUniqueId()), user -> {
                    if (!NBTTagHandler.getString(clickedItem, "language_accessor").equalsIgnoreCase(user.getLanguage())) {
                        user.setLanguage(NBTTagHandler.getString(clickedItem, "language_accessor"));
                        try {
                            this.userUpdateRequest.executeRequest(user, this.tokenQuery.getToken());
                            this.userStorage.storeUser(user, player.getUniqueId());
                            ChatAlertLibrary.infoAlert(player,
                                    this.translator.getUnspacedField(
                                            user.getLanguage(),
                                            "authentication_language_update_success"
                                    ).replace("%%language%%", this.translator.getUnspacedField(user.getLanguage(), "commons_language_placeholder"))
                            );
                        } catch (Unauthorized | BadRequest | NotFound | InternalServerError error) {
                            ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(user.getLanguage(), "authentication_language_update_error"));
                            Bukkit.getLogger().log(Level.SEVERE, "[Commons] Something went wrong updating player {0} ({1}): {2}",
                                    new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
                        }
                    } else {
                        ChatAlertLibrary.errorChatAlert(player,
                                this.translator.getUnspacedField(user.getLanguage(), "authentication_language_update_same") + "."
                        );
                        player.closeInventory();
                    }
                });
            }
        }
    }
}
