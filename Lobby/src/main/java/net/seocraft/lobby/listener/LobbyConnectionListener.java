package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.commons.bukkit.user.LobbyConnectionEvent;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.ChatGlyphs;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.hotbar.HotbarItemCollection;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class LobbyConnectionListener implements Listener {

    @Inject private HotbarItemCollection hotbarItemCollection;
    @Inject private TranslatableField translator;
    @Inject private TeleportManager teleportManager;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private FriendshipProvider friendshipProvider;

    @EventHandler
    public void lobbyConnectionListener(LobbyConnectionEvent event) {
        Player player = event.getPlayer();
        User playerRecord = event.getPlayerRecord();

        this.teleportManager.spawnTeleport(player, null, true);

        this.hotbarItemCollection.setupPlayerHotbar(
                player,
                playerRecord
        );

        // Detect when player has hiding gadget enabled
        if (playerRecord.isHiding()) {
            Bukkit.getOnlinePlayers().forEach(onlinePlayer ->  {
                GameSession handler;
                try {
                    handler = this.gameSessionManager.getCachedSession(onlinePlayer.getName());
                    if (
                            handler != null &&
                                    !this.friendshipProvider.checkFriendshipStatus(playerRecord.getId(), handler.getPlayerId()) &&
                                    !onlinePlayer.hasPermission("commons.staff.vanish")
                    ) {
                        player.hidePlayer(onlinePlayer);
                    }

                    if (this.friendshipProvider.hasUnreadRequests(playerRecord.getId())) {
                        player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                        // Base alert component
                        TextComponent baseComponent = new TextComponent(
                                this.translator.getUnspacedField(playerRecord.getLanguage(), "commons_friends_pending_requests") + ". "
                        );
                        baseComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                        TextComponent clickComponent = new TextComponent(
                                this.translator.getUnspacedField(playerRecord.getLanguage(), "commons_friends_pending_click")
                        );
                        clickComponent.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        clickComponent.setHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder( ChatColor.YELLOW +
                                        this.translator.getUnspacedField(playerRecord.getLanguage(), "commons_friends_pending_click")
                                ).create()
                        ));
                        clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends requests"));
                        baseComponent.addExtra(clickComponent);
                        player.sendMessage(baseComponent);

                        player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                    }
                } catch (IOException e) {
                    ChatAlertLibrary.errorChatAlert(player);
                }

            });
        }



    }
}
