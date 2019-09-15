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
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.UserStorageProvider;

public class PlayerJoinListener implements Listener {

    @Inject private GameSessionManager gameSessionManager;
    @Inject private OnlineStatusManager onlineStatusManager;
    @Inject private UserStorageProvider userStorageProvider;

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(player.getName()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                try {
                    this.gameSessionManager.createGameSession(userAsyncResponse.getResponse(),  player.getAddress().getHostName(), "1.8.9"); //TODO: Get user version
                    this.onlineStatusManager.setPlayerOnlineStatus(userAsyncResponse.getResponse().getId(), true);
                } catch (JsonProcessingException e) {
                    player.disconnect(
                            new TextComponent(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + e.getClass().getSimpleName())
                    );
                }
            } else {
                player.disconnect(
                        new TextComponent(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + userAsyncResponse.getThrowedException().getClass().getSimpleName())
                );
            }
        });
    }
}
