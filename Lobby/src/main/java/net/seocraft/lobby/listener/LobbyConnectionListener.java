package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.old.friend.FriendshipHandler;
import net.seocraft.commons.bukkit.old.user.LobbyConnectionEvent;
import net.seocraft.lobby.menu.HotbarItemCollection;
import net.seocraft.lobby.teleport.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LobbyConnectionListener implements Listener {

    @Inject private HotbarItemCollection hotbarItemCollection;
    @Inject private TeleportHandler teleportHandler;
    @Inject private GameSessionManager gameSessionManager;
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
                GameSession handler = this.gameSessionManager.getCachedSession(onlinePlayer.getName());
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
