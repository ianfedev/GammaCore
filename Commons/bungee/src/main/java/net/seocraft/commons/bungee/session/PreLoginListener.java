package net.seocraft.commons.bungee.session;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bungee.CommonsBungee;

public class PreLoginListener implements Listener {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private CommonsBungee commonsBungee;

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {

        PendingConnection connection = event.getConnection();

        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(connection.getName()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                if (!userAsyncResponse.getResponse().isPremium()) connection.setOnlineMode(false);
                event.completeIntent(this.commonsBungee);
            } else {
                if (userAsyncResponse.getStatusCode() != 404) {
                    connection.disconnect(
                            new TextComponent(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + userAsyncResponse.getThrowedException().getClass().getSimpleName())
                    );
                } else {
                    connection.setOnlineMode(false);
                    event.completeIntent(this.commonsBungee);
                }
            }
        });


    }
}
