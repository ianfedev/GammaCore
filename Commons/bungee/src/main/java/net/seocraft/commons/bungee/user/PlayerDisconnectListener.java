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
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.session.MinecraftSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bungee.CommonsBungee;

import java.io.IOException;
import java.util.logging.Level;

public class PlayerDisconnectListener implements Listener {

    @Inject private OnlineStatusManager onlineStatusManager;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private RedisClient redisClient;
    @Inject private MinecraftSessionManager minecraftSessionManager;
    @Inject private CommonsBungee commonsBungee;
    @Inject private Messager messager;

    @EventHandler
    public void playerDisconnectEvent(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        try {
            User user = this.userStorageProvider.findUserByNameSync(player.getName());
            Channel<User> userChannel = this.messager.getChannel("ac_logout", User.class);
            userChannel.sendMessage(user);
            this.onlineStatusManager.setPlayerOnlineStatus(user.getId(), false);
            this.minecraftSessionManager.disconnectSession(user.getId());
            this.redisClient.removeFromSet("premium_connect", user.getId());
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ex) {
            this.commonsBungee.getLogger().log(Level.SEVERE, "[CommonsBungee] There was an error disconnecting a player.", ex);
            player.disconnect(
                    new TextComponent(ChatColor.RED + "Error when logging out, please login and logout to refresh your \n\n session status. \n\n" + ChatColor.GRAY + "Error Type: " + ex.getClass().getSimpleName())
            );
        }

    }
}
