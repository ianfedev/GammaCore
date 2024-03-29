package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.creator.board.ScoreboardManager;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorHologramManager;
import net.seocraft.api.bukkit.user.UserLobbyMessageHandler;
import net.seocraft.api.bukkit.utils.ChatGlyphs;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.user.LobbyConnectionEvent;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.Lobby;
import net.seocraft.lobby.board.LobbyScoreboardTask;
import net.seocraft.lobby.hotbar.HotbarItemCollection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.logging.Level;

public class LobbyConnectionListener implements Listener {

    @Inject private HotbarItemCollection hotbarItemCollection;
    @Inject private TranslatableField translator;
    @Inject private ScoreboardManager scoreboardManager;
    @Inject private Lobby instance;
    @Inject private TeleportManager teleportManager;
    @Inject private CloudManager cloudManager;
    @Inject private SelectorHologramManager hologramManager;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private FriendshipProvider friendshipProvider;
    @Inject private UserLobbyMessageHandler userLobbyMessageHandler;

    @EventHandler
    public void lobbyConnectionListener(LobbyConnectionEvent event) {
        Player player = event.getPlayer();
        User playerRecord = event.getPlayerRecord();
        this.teleportManager.spawnTeleport(player, null, true);
        this.hologramManager.showSelectorHologram(playerRecord);

        this.userLobbyMessageHandler.alertUserJoinMessage(playerRecord);

        this.hotbarItemCollection.setupPlayerHotbar(
                player,
                playerRecord
        );

        if (!playerRecord.getGameSettings().getAdminChat().isActive()) {
            Bukkit.getScheduler().runTaskLater(this.instance, () -> {

                try {
                    User finalUser = this.userStorageProvider.getCachedUserSync(player.getDatabaseIdentifier());

                    if (!finalUser.getGameSettings().getAdminChat().isActive() && player.hasPermission("commons.staff.chat")) {
                        TextComponent message = new TextComponent(this.translator.getUnspacedField(playerRecord.getLanguage(), "commons_ac_reminder") + ". ");
                        message.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                        TextComponent hover = new TextComponent(this.translator.getUnspacedField(playerRecord.getLanguage(), "commons_ac_reminder_click"));
                        hover.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                        hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acs"));
                        hover.setHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder(
                                                net.md_5.bungee.api.ChatColor.YELLOW +
                                                        this.translator.getUnspacedField(
                                                                playerRecord.getLanguage(),
                                                                "commons_ac_reminder_click"
                                                        )
                                        ).create()
                                )
                        );
                        message.addExtra(hover);
                        player.sendMessage(message);
                    }

                } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) {}

            },  30 * 20L);
        }

        // Detect when player has hiding gadget enabled
        if (playerRecord.getGameSettings().getGeneral().isHidingPlayers()) {
            Bukkit.getOnlinePlayers().forEach(onlinePlayer ->  {
                try {
                    if (
                            !this.friendshipProvider.checkFriendshipStatus(playerRecord.getId(), player.getDatabaseIdentifier()) &&
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
                    Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error teleporting user to lobby.", e);
                    ChatAlertLibrary.errorChatAlert(player);
                }

            });
        }

        // TODO: Remove after TestDay
        player.sendMessage(
                ChatColor.GREEN + this.translator.getUnspacedField(event.getPlayerRecord().getLanguage(), "commons_lobby_beta")
        );

        if (this.instance.getConfig().getBoolean("board.default"))
            new LobbyScoreboardTask(
                    scoreboardManager, cloudManager, translator, bukkitAPI,
                    player.getName(),
                    playerRecord
            ).runTaskTimer(this.instance, 0, 20*60);
    }

}
