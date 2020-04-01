package net.seocraft.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryInteractionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void inventoryInteractionEvent(InventoryClickEvent event) {
        System.out.println("Final event triggered");
        event.setCancelled(true);
    }
}
