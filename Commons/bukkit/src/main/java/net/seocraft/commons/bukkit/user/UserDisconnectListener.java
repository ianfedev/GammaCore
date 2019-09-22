package net.seocraft.commons.bukkit.user;

import com.google.inject.Inject;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.logging.Level;

public class UserDisconnectListener implements Listener {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private ServerManager serverManager;
    @Inject private CommonsBukkit commonsBukkit;
    @Inject private GameSessionManager gameSessionManager;

    @EventHandler
    public void disconnectListenerEvent(PlayerQuitEvent event) {
        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(event.getPlayer().getName()), userAsyncResponse -> {
            boolean disconnection = false;
            while (!disconnection) {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    Server updatableRecord = this.commonsBukkit.getServerRecord();
                    updatableRecord.getOnlinePlayers().remove(userAsyncResponse.getResponse().getId());
                    try {
                        this.serverManager.updateServer(
                                updatableRecord
                        );
                        this.commonsBukkit.setServerRecord(updatableRecord);
                        disconnection = true;
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException error) {
                        Bukkit.getLogger().log(Level.SEVERE, "[Commons] Error logging out player from server. ({0})", error.getMessage());
                    }
                } else {
                    try {
                        User user = this.userStorageProvider.findUserByNameSync(event.getPlayer().getName());
                        Server updatableRecord = this.commonsBukkit.getServerRecord();
                        updatableRecord.getOnlinePlayers().remove(user.getId());
                        this.serverManager.updateServer(
                                updatableRecord
                        );
                        this.commonsBukkit.setServerRecord(updatableRecord);
                        disconnection = true;
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) {}
                }
            }
        });
    }
}
