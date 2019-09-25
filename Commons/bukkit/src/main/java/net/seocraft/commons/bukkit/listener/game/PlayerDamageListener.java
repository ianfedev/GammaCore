package net.seocraft.commons.bukkit.listener.game;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.match.Match;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.io.IOException;

public class PlayerDamageListener implements Listener {

    @Inject private CoreGameManagement coreGameManagement;

    @EventHandler
    public void gameDamageListener(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity()).getPlayer();
            if (
                    this.coreGameManagement.getSpectatingPlayers().contains(player) ||
                    this.coreGameManagement.getWaitingPlayers().contains(player)
            ) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                    Match match = this.coreGameManagement.getPlayerMatch(player);
                    if (match != null) {
                        try {
                            if (this.coreGameManagement.getWaitingPlayers().contains(player)) {
                                player.teleport(this.coreGameManagement.getLobbyLocation(match));
                                event.setCancelled(true);
                                return;
                            }

                            if (this.coreGameManagement.getSpectatingPlayers().contains(player)) {
                                player.teleport(this.coreGameManagement.getSpectatorSpawnLocation(match));
                                event.setCancelled(true);
                                return;
                            }
                        } catch (IOException e) {
                            player.kickPlayer(ChatColor.RED + "There was an error with your match. " + ChatColor.GRAY + "(" + e.getMessage() + ")");
                        }
                    } else {
                        player.kickPlayer(ChatColor.RED + "There was an error with your match. " + ChatColor.GRAY + "(Unknown match)");
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
            if (
                    this.coreGameManagement.getSpectatingPlayers().contains(player) ||
                            this.coreGameManagement.getWaitingPlayers().contains(player)
            ) {
                event.setCancelled(true);
            }
        }
    }
}
