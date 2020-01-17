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
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bungee.CommonsBungee;
import net.seocraft.commons.bungee.server.BungeeCloudManager;

import java.io.IOException;
import java.util.logging.Level;

public class PreLoginListener implements Listener {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private CommonsBungee commonsBungee;

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {

        PendingConnection connection = event.getConnection();
        event.registerIntent(commonsBungee);
        User user = null;
        try {
            user = this.userStorageProvider.findUserByNameSync(connection.getName());
            if (!user.isPremium()) connection.setOnlineMode(false);
        } catch (Unauthorized | BadRequest | InternalServerError | IOException e) {
            this.commonsBungee.getLogger().log(Level.WARNING, "[Commons] There was an error logging a player.", e);
            connection.disconnect(
                    new TextComponent(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + e.getClass().getSimpleName())
            );
        } catch (NotFound e) {
            connection.setOnlineMode(false);
        }
        event.completeIntent(commonsBungee);

        if (user != null && user.isPremium()) {
            BungeeCloudManager.sendPlayerToGroup(connection.getName(), connection.getUniqueId().toString(), "main_lobby");
        }

    }
}
