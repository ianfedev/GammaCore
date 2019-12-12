package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    @Inject private TeleportManager teleportManager;

    public void playerDeathEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                this.teleportManager.spawnTeleport(player, null,false);
            }

            event.setCancelled(true);
        }
    }

}
