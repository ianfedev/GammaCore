package net.seocraft.commons.bukkit.listener.game;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchAssignation;
import net.seocraft.api.bukkit.game.match.MatchDataProvider;
import net.seocraft.api.bukkit.game.match.PlayerType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.io.IOException;

public class PlayerDamageListener implements Listener {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private MatchDataProvider matchDataProvider;

    @EventHandler
    public void gameDamageListener(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity()).getPlayer();
            MatchAssignation match = this.matchDataProvider.getPlayerMatch(player.getDatabaseIdentifier());
            if (match != null) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                    try {
                        if (match.getPlayerType() == PlayerType.HOLDING) {
                            player.teleport(this.coreGameManagement.getLobbyLocation(match.getMatch()));
                            event.setCancelled(true);
                            return;
                        }

                        if (match.getPlayerType() == PlayerType.SPECTATOR) {
                            player.teleport(this.coreGameManagement.getSpectatorSpawnLocation(match.getMatch()));
                            event.setCancelled(true);
                            return;
                        }

                    } catch (IOException e) {
                        player.kickPlayer(ChatColor.RED + "There was an error with your match. " + ChatColor.GRAY + "(" + e.getMessage() + ")");
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            MatchAssignation match = this.matchDataProvider.getPlayerMatch(player.getDatabaseIdentifier());
            if (match != null) event.setCancelled(true);
        }
    }
}
