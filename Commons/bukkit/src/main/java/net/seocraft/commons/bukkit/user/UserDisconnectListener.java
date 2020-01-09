package net.seocraft.commons.bukkit.user;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.creator.intercept.PacketManager;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameLoginManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.creator.hologram.Hologram;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class UserDisconnectListener implements Listener {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private GameLoginManager gameLoginManager;
    @Inject private CoreGameManagement coreGameManagement;
    @Inject private ServerManager serverManager;
    @Inject private CommonsBukkit commonsBukkit;
    @Inject private PacketManager packetManager;

    @EventHandler
    public void disconnectListenerEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Set<Hologram> playerHolograms = new HashSet<>(player.getLinkedHolograms());
        playerHolograms.forEach(Hologram::destroy);
        player.stopPlayingSong();
        this.packetManager.uninjectPlayer(event.getPlayer());
        CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(event.getPlayer().getName()), userAsyncResponse -> {
            boolean disconnection = false;
            while (!disconnection) {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    Server updatableRecord = this.commonsBukkit.getServerRecord();
                    User user = userAsyncResponse.getResponse();

                    updatableRecord.getOnlinePlayers().remove(user.getId());
                    try {
                        this.serverManager.updateServer(updatableRecord);
                        this.commonsBukkit.setServerRecord(updatableRecord);

                        if (this.commonsBukkit.getServerRecord().getServerType().equals(ServerType.GAME)) {
                            Match playerMatch = this.coreGameManagement.getPlayerMatch(user);

                            if (playerMatch != null) {
                                this.gameLoginManager.matchPlayerLeave(
                                        playerMatch,
                                        user,
                                        player
                                );
                            }
                        }
                        disconnection = true;
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException error) {
                        Bukkit.getLogger().log(Level.SEVERE, "[Commons] Error logging out player from server. ({0})", error.getMessage());
                    }
                } else {
                    try {
                        User user = this.userStorageProvider.findUserByNameSync(event.getPlayer().getName());
                        Server updatableRecord = this.commonsBukkit.getServerRecord();
                        updatableRecord.getOnlinePlayers().remove(user.getId());
                        this.serverManager.updateServer(updatableRecord);
                        this.commonsBukkit.setServerRecord(updatableRecord);

                        if (this.commonsBukkit.getServerRecord().getServerType().equals(ServerType.GAME)) {
                            Match playerMatch = this.coreGameManagement.getPlayerMatch(user);

                            if (playerMatch != null) {
                                this.gameLoginManager.matchPlayerLeave(
                                        playerMatch,
                                        user,
                                        player
                                );
                            }
                        }
                        disconnection = true;
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) {}
                }
            }
        });
    }

}
