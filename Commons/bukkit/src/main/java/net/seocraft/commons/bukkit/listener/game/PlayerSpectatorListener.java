package net.seocraft.commons.bukkit.listener.game;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.event.GameSpectatorSetEvent;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.match.*;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.game.management.menu.SpectatorToolbar;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class PlayerSpectatorListener implements Listener {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private MatchDataProvider matchDataProvider;
    @Inject private TranslatableField translatableField;
    @Inject private MatchMapProvider matchMapProvider;
    @Inject private MatchAssignationProvider matchAssignationProvider;
    @Inject private UserFormatter userFormatter;
    @Inject private BukkitAPI bukkitAPI;

    @EventHandler
    public void playerSpectatorListener(GameSpectatorSetEvent event) {

        Match gameMatch = event.getMatch();
        Player player = event.getPlayer();
        User user = event.getUser();

        try {
            if (gameMatch.getStatus().equals(MatchStatus.WAITING)) {
                player.teleport(this.matchMapProvider.getLobbyLocation(gameMatch));
            } else {
                player.teleport(this.matchMapProvider.getSpectatorSpawnLocation(gameMatch));
            }

            this.matchAssignationProvider.assignPlayer(user.getId(), gameMatch, PlayerType.SPECTATOR);

            if (event.isManual()) {
                this.matchDataProvider.getMatchParticipants(gameMatch).forEach(onlinePlayer -> {
                    Player playerRecord = Bukkit.getPlayer(onlinePlayer.getUsername());
                    if (playerRecord != null && playerRecord.hasPermission("commons.staff.match.spectate.messages")) {
                        ChatAlertLibrary.infoAlert(
                                playerRecord,
                                this.translatableField.getUnspacedField(
                                        onlinePlayer.getLanguage(),
                                        "commons_spectator_join"
                                ).replace("%%player%%", this.userFormatter.getUserColor(user, this.bukkitAPI.getConfig().getString("realm")) + ChatColor.AQUA)
                        );
                    }
                });
            }

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();

            MatchAssignation matchAssignation = this.matchDataProvider.getPlayerMatch(user.getId());

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                if (matchAssignation != null && matchAssignation.getPlayerType() != PlayerType.SPECTATOR) {
                    onlinePlayer.hidePlayer(player);
                } else {
                    // TODO: Ghost mode
                }
            });

            if (!event.isCustom()) {
                // TODO: Create spectator tools and create implementations
            }

            player.getInventory().setItem(7, SpectatorToolbar.getPlayAgainItem(user.getLanguage(), translatableField, event.isManual()));
            player.getInventory().setItem(8, SpectatorToolbar.getLobbyReturnItem(user.getLanguage(), translatableField));

        } catch (IOException e) {
            player.kickPlayer(
                    ChatColor.RED +
                            this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_spectator_error"
                            )
            );
        }
    }
}
