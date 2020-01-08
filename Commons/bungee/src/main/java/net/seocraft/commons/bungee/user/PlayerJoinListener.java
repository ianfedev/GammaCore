package net.seocraft.commons.bungee.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.user.UserStorageProvider;

public class PlayerJoinListener implements Listener {

    @Inject private OnlineStatusManager onlineStatusManager;
    @Inject private UserStorageProvider userStorageProvider;

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(player.getName()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                this.onlineStatusManager.setPlayerOnlineStatus(userAsyncResponse.getResponse().getId(), true);
            } else {
                if (userAsyncResponse.getStatusCode() != 404) {
                    player.disconnect(
                            new TextComponent(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + userAsyncResponse.getThrowedException().getClass().getSimpleName())
                    );
                }
            }
        });
    }
}
