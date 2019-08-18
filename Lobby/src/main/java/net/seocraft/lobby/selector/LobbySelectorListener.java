package net.seocraft.lobby.selector;

import com.google.inject.Inject;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.logging.Level;

public class LobbySelectorListener implements Listener {

    @Inject private GameSessionManager gameSessionManager;
    @Inject private CommonsBukkit commonsBukkit;
    @Inject private TranslatableField translatableField;
    @Inject private UserStorageProvider userStorageProvider;

    @EventHandler
    public void lobbySelectorListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack clickedItem = player.getItemInHand();
        try {
            GameSession session = this.gameSessionManager.getCachedSession(player.getName());
            if (session != null) {
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(session.getPlayerId()), userAsyncResponse -> {
                    if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User user = userAsyncResponse.getResponse();
                        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            if (NBTTagHandler.hasString(clickedItem, "lobby_selector")) {
                                switch (NBTTagHandler.getString(clickedItem, "lobby_selector")) {
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

                                        break;
                                    }
                                }
                            } else if (NBTTagHandler.hasString(clickedItem, "lobby_pages")) {

                            }
                        }
                    } else {

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
