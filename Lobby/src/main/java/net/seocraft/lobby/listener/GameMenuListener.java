package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.lobby.game.GameMenuManagerImp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class GameMenuListener implements Listener {

    @Inject private GameSessionManager gameSessionManager;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private GameMenuManagerImp gameMenuHandlerImp;

    @EventHandler
    public void gameMenuListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack clickedItem = player.getItemInHand();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Detect if element has designed tag
            if (NBTTagHandler.hasString(clickedItem, "accessor") &&
                    (
                            NBTTagHandler.getString(clickedItem, "accessor").equalsIgnoreCase("game_menu")
                    )
            ) {
                GameSession session = null;
                try {
                    session = this.gameSessionManager.getCachedSession(player.getName());
                    if (session != null) {
                        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(session.getPlayerId()), userAsyncResponse -> {
                            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                                User user = userAsyncResponse.getResponse();
                                this.gameMenuHandlerImp.loadGameMenu(player, user.getLanguage());
                                event.setCancelled(true);
                            } else {
                                ChatAlertLibrary.errorChatAlert(player, null);
                            }
                        });
                    } else {
                        ChatAlertLibrary.errorChatAlert(player);
                    }
                } catch (IOException e) {
                    ChatAlertLibrary.errorChatAlert(player);
                }

            }
        }
    }
}
