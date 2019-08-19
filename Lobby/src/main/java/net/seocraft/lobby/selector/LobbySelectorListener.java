package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.logging.Level;

public class LobbySelectorListener implements Listener {

    @Inject private GameSessionManager gameSessionManager;
    @Inject private CommonsBukkit commonsBukkit;
    @Inject private TranslatableField translatableField;
    @Inject private CommonsBukkit instance;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private LobbySelectorMenu lobbySelectorMenu;
    @Inject private CloudManager cloudManager;

    @EventHandler
    public void lobbySelectorListener(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        HumanEntity entity = event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if ((entity instanceof Player)) {
            Player player = (Player) entity;
            try {
                GameSession session = this.gameSessionManager.getCachedSession(player.getName());
                if (session != null) {
                    CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(session.getPlayerId()), userAsyncResponse -> {
                        if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User user = userAsyncResponse.getResponse();
                            if (event.getClick().equals(ClickType.LEFT)) {
                                if (NBTTagHandler.hasString(clickedItem, "lobby_selector_opt")) {
                                    switch (NBTTagHandler.getString(clickedItem, "lobby_selector_opt")) {
                                        case "FULL": {
                                            ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_lobby_selector_full"));
                                            Bukkit.getScheduler().runTask(this.commonsBukkit, player::closeInventory);
                                            break;
                                        }
                                        case "ACTUAL": {
                                            ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_lobby_selector_already"));
                                            Bukkit.getScheduler().runTask(this.commonsBukkit, player::closeInventory);
                                            break;
                                        }
                                        case "CLOSE": {
                                            Bukkit.getScheduler().runTask(this.commonsBukkit, player::closeInventory);
                                            break;
                                        }
                                        default: {
                                            this.cloudManager.sendPlayerToServer(player, NBTTagHandler.getString(clickedItem, "lobby_selector_opt"));
                                            break;
                                        }
                                    }
                                } else if (NBTTagHandler.hasString(clickedItem, "lobby_pages")) {
                                    Inventory newInventory = this.lobbySelectorMenu.getLobbyMenuSync(
                                            user.getLanguage(),
                                            this.cloudManager.getGroupLobbies(
                                                    this.instance.getServerRecord().getSlug().split("-")[0]
                                            ),
                                            Bukkit.getServerName(),
                                            Integer.parseInt(NBTTagHandler.getString(clickedItem, "lobby_pages"))
                                    );
                                    Bukkit.getScheduler().runTask(this.instance, () -> player.openInventory(newInventory));
                                }
                            } else {
                                event.setCancelled(true);
                            }
                        } else {
                            Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error retrieving session of player {0}.", player.getName());
                            ChatAlertLibrary.errorChatAlert(player);
                        }
                    });
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error retrieving session of player {0}.", player.getName());
                    ChatAlertLibrary.errorChatAlert(player);
                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error retrieving session of player {0}. ({1})", new Object[]{player.getName(), e.getMessage()});
            }
        }

    }
}
