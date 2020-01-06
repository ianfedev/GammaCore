package net.seocraft.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryInteractionListener implements Listener {

    @EventHandler
    public void inventoryInteractionEvent(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
