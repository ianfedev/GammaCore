package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class PlayerDamageListener implements Listener {

    @Inject private TeleportManager teleportManager;

    @EventHandler
    public void playerDeathEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                this.teleportManager.spawnTeleport(player, null,false);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) event.setCancelled(true);
    }

    @EventHandler
    public void weatherChangeCycle(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

}
