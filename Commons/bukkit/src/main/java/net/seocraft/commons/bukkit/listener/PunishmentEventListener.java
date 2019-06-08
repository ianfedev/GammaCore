package net.seocraft.commons.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PunishmentEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void punishmentJoinListener(PlayerJoinEvent event) {

    }
}
