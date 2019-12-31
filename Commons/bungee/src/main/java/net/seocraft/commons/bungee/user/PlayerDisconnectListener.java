package net.seocraft.commons.bungee.user;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.UserStorageProvider;

public class PlayerDisconnectListener implements Listener {


    @Inject private GameSessionManager gameSessionManager;
    @Inject private OnlineStatusManager onlineStatusManager;
    @Inject private UserStorageProvider userStorageProvider;

    @EventHandler
    public void playerDisconnectEvent(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(player.getName()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                this.gameSessionManager.removeGameSession(player.getName());
                this.onlineStatusManager.setPlayerOnlineStatus(userAsyncResponse.getResponse().getId(), false);
            } else {
                player.disconnect(
                        new TextComponent(ChatColor.RED + "Error when logging out, please login and logout to refresh your \n\n session status. \n\n" + ChatColor.GRAY + "Error Type: " + userAsyncResponse.getThrowedException().getClass().getSimpleName())
                );
            }
        });


    }
}
