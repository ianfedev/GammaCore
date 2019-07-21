package net.seocraft.commons.bukkit.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
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

import java.io.IOException;
import java.util.logging.Level;

public class AuthenticationLanguageSelectListener implements Listener {

    @Inject private TranslatableField translator;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private UserStorageProvider userStorageProvider;

    @EventHandler(priority = EventPriority.LOWEST)
    public void authenticationLanguageSelectListener(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        HumanEntity entity = event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if ((entity instanceof Player)) {
            Player player = (Player) entity;
            if (event.getClick().equals(ClickType.LEFT) && NBTTagHandler.hasString(clickedItem, "language_accessor")) {
                try {
                    CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(this.gameSessionManager.getCachedSession(player.getName()).getPlayerId()), asyncResponse -> {
                        if (asyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User user = asyncResponse.getResponse();
                            if (!NBTTagHandler.getString(clickedItem, "language_accessor").equalsIgnoreCase(user.getLanguage())) {
                                user.setLanguage(NBTTagHandler.getString(clickedItem, "language_accessor"));
                                try {
                                    this.userStorageProvider.updateUser(user);
                                    ChatAlertLibrary.infoAlert(player,
                                            this.translator.getUnspacedField(
                                                    user.getLanguage(),
                                                    "authentication_language_update_success"
                                            ).replace("%%language%%", this.translator.getUnspacedField(user.getLanguage(), "commons_language_placeholder"))
                                    );
                                } catch (Unauthorized | BadRequest | NotFound | InternalServerError | JsonProcessingException error) {
                                    ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(user.getLanguage(), "authentication_language_update_error"));
                                    Bukkit.getLogger().log(Level.SEVERE, "[Commons] Something went wrong updating player {0} ({1}): {2}",
                                            new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
                                }
                                ChatAlertLibrary.errorChatAlert(player,
                                        this.translator.getUnspacedField(user.getLanguage(), "authentication_language_update_same") + "."
                                );
                                player.closeInventory();
                            }
                        } else {
                            ChatAlertLibrary.errorChatAlert(player);
                        }
                    });
                } catch (IOException e) {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            }
        }
    }
}
