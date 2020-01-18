package net.seocraft.lobby.hotbar;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.profile.ProfileManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.selector.LobbySelectorMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class HotbarListener implements Listener {

    @Inject private LobbySelectorMenu lobbySelectorMenu;
    @Inject private ProfileManager profileManager;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;
    @Inject private CommonsBukkit instance;
    @Inject private CloudManager cloudManager;
    @Inject private LobbyGameManager gameMenuHandlerImp;

    @EventHandler
    public void gameMenuListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack clickedItem = player.getItemInHand();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Detect if element has designed tag
            if (clickedItem != null && NBTTagHandler.hasString(clickedItem, "accessor")) {
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();
                        switch (NBTTagHandler.getString(clickedItem, "accessor")) {
                            case "game_menu": {
                                this.gameMenuHandlerImp.loadGameMenu(player, user.getLanguage());
                                return;
                            }
                            case "profile": {
                                this.profileManager.openMainMenu(user);
                                return;
                            }
                            case "lobby_selector": {
                                player.sendMessage(ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_wait"));
                                CallbackWrapper.addCallback(this.lobbySelectorMenu.getLobbyMenu(
                                        user.getLanguage(),
                                        this.cloudManager.getGroupLobbies(
                                                this.instance.getServerRecord().getSlug().split("-")[0]
                                        ),
                                        this.instance.getServerRecord().getSlug(),
                                        1
                                ), asyncInventoryResponse -> {
                                    if (asyncInventoryResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                        Bukkit.getScheduler().runTask(this.instance, () -> player.openInventory(asyncInventoryResponse.getResponse()));
                                    } else {
                                        Bukkit.getLogger().log(Level.SEVERE, "[Lobby] Error when opening lobby. ({0})", asyncInventoryResponse.getThrowedException().getMessage());
                                        ChatAlertLibrary.errorChatAlert(
                                                player,
                                                this.translatableField.getUnspacedField(user.getLanguage(), "commons_Lobby_selector_error") + "."
                                        );
                                    }
                                });
                                return;
                            }
                        }
                        event.setCancelled(true);
                    } else {
                        ChatAlertLibrary.errorChatAlert(player);
                    }
                });
            }
        }
    }
}
