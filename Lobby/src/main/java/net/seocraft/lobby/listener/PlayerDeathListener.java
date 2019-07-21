package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDeathListener implements Listener {

    @Inject private TeleportManager teleportManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDeathEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getHealth() < 1) {
                event.setCancelled(true);
                this.teleportManager.spawnTeleport(player, null,false);
            }
        }
    }

}
