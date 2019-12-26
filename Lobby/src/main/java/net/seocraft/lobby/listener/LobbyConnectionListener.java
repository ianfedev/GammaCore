package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.creator.board.ScoreboardManager;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.creator.npc.NPCManager;
import net.seocraft.api.bukkit.creator.npc.action.ClickType;
import net.seocraft.api.bukkit.creator.npc.entity.player.NPCPlayer;
import net.seocraft.api.bukkit.creator.npc.event.NPCInteractEvent;
import net.seocraft.api.bukkit.creator.skin.CraftSkinProperty;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.commons.bukkit.user.LobbyConnectionEvent;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.ChatGlyphs;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.Lobby;
import net.seocraft.lobby.board.LobbyScoreboardTask;
import net.seocraft.lobby.hotbar.HotbarItemCollection;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.io.IOException;

public class LobbyConnectionListener implements Listener {

    @Inject private HotbarItemCollection hotbarItemCollection;
    @Inject private TranslatableField translator;
    @Inject private ScoreboardManager scoreboardManager;
    @Inject private Lobby instance;
    @Inject private TeleportManager teleportManager;
    @Inject private CloudManager cloudManager;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private FriendshipProvider friendshipProvider;
    @Inject private NPCManager npcManager;

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


        new LobbyScoreboardTask(
                scoreboardManager, cloudManager, translator, bukkitAPI,
                player.getName(),
                playerRecord
        ).runTaskTimer(this.instance, 0, 20*60);
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("npc")) {

            ArmorStand as = (ArmorStand) event.getPlayer().getLocation().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.ARMOR_STAND);
            as.setGravity(false);


            NPCPlayer npcPlayer = (NPCPlayer) this.npcManager.createPlayerNPC(this.instance, event.getPlayer().getLocation(), ChatColor.YELLOW + "" + ChatColor.BOLD + "TNT Popper \n" + "POPPER" , new CraftSkinProperty(
                    "xu9wKQyRXY5grIoBdyfXSrdD/bKdw4dMKTMM05Gq7wOZrRJD56fjRgNYkDQqH/N/c41Dy7JE71XxWcEsN6izEr1AgdODuCm6yYsUu59RS68bc9Gese9COUKYO7gYdlPCVtzuqEPHxgDzMVq+kKrOMLSoUYlCarpeuGNNRErO30uGj9xJ9ZFRJc9dBSMoC4iwu/9viAtZsI5Nd+s0c9iyxAGGO4bwcJQPIIQ9lZ6CFWQlF6vaIJR+qzaXGFXHCZXyyuS56t4lJAM0N6UQNEe364xjQjNVWh1KnlIgc0zyZsk4xGwLgPHlBa3NWfAs/tMNG/+UM56PxdXBD1FyPTqnOjhaja/C0tHJXZc9k3kPLoSdUH5+gWx+UXG7ZqhBF9xAuZjqRQDyH1H8L+VcQvDQ5qpBqk5sUugaHuU/5Aat5+zELXByrUxJK09NZgwRdykevA8aoBxcT+nuo50f0Ih676yES5aCuXrG2TniCBOm2jKi9IsMSdC4jx/C+rpYAUMFEcVNfXqmW4Qv38yrD1Vh5STLb98SVr0KlD0a3pO4pyAaW0ZoTYh68DJ7CozAb6DeAmTbIhGgoUi24753PbCO0C2mlpwiBgSud3EnEd72jCbxTZyx8ry9bXSMowXNO2wkEfJE+ZHDQTCZ0Ofi99AvlG3+t6I3Q7dHULj5jG4JvrU=",
                    "eyJ0aW1lc3RhbXAiOjE1NzU1MTQ3NzQzOTgsInByb2ZpbGVJZCI6IjZkYTEyZDExYjNjODQzOGQ5OTkwYjFkMjE2NjNkNTVmIiwicHJvZmlsZU5hbWUiOiJlVW5jbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E5YWZiYWY3NjIyMzc4NWIzY2IzODFiN2MzMjk0NWUzYTIxNTdiMmJhYjM3MmFmODJhZjc4YzNmNzM5ZDcxNGEifX19")
            ).addActionHandler((npc, npcEvent) -> {
                if (npcEvent instanceof NPCInteractEvent) {
                    NPCInteractEvent interactEvent = (NPCInteractEvent) npcEvent;
                    if (interactEvent.getClickType() == ClickType.RIGHT_CLICK) {
                        npc.setPassenger(interactEvent.getPlayer());
                    }
                }
            });
            npcPlayer.setFrozen(false);
            npcPlayer.setControllable(true);

        }
    }

}
