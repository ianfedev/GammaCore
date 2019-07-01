package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.friend.FriendshipHandler;
import net.seocraft.commons.bukkit.user.LobbyConnectionEvent;
import net.seocraft.lobby.menu.HotbarItemCollection;
import net.seocraft.lobby.teleport.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LobbyConnectionListener implements Listener {

    @Inject private HotbarItemCollection hotbarItemCollection;
    @Inject private TeleportHandler teleportHandler;
    @Inject private SessionHandler sessionHandler;
    @Inject private FriendshipHandler friendshipHandler;

    @EventHandler
    public void lobbyConnectionListener(LobbyConnectionEvent event) {
        Player player = event.getPlayer();
        User playerRecord = event.getPlayerRecord();

        this.teleportHandler.spawnTeleport(player, null, true);

        this.hotbarItemCollection.setupPlayerHotbar(
                player,
                playerRecord
        );

        // Detect when player has hiding gadget enabled
        if (playerRecord.isHiding()) {
            Bukkit.getOnlinePlayers().forEach(onlinePlayer ->  {
                GameSession handler = this.sessionHandler.getCachedSession(onlinePlayer.getName());
                if (
                        handler != null &&
                        !this.friendshipHandler.checkFriendshipStatus(playerRecord.id(), handler.getPlayerId()) &&
                                !onlinePlayer.hasPermission("commons.staff.vanish")
                ) {
                    player.hidePlayer(onlinePlayer);
                }
            });
        }

    }
}
