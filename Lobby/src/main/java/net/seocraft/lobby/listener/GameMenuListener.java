package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.lobby.game.GameMenuHandlerImp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GameMenuListener implements Listener {

    @Inject private SessionHandler sessionHandler;
    @Inject private UserStoreHandler userStoreHandler;
    @Inject private GameMenuHandlerImp gameMenuHandlerImp;

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
                GameSession session = this.sessionHandler.getCachedSession(player.getName());
                if (session != null) {
                    CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(session.getPlayerId()), userAsyncResponse -> {
                        if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User user = userAsyncResponse.getResponse();
                            this.gameMenuHandlerImp.loadGameMenu(player, user.getLanguage());
                            event.setCancelled(true);
                        } else {
                            ChatAlertLibrary.errorChatAlert(player, null);
                        }
                    });
                } else {
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            }
        }
    }
}
