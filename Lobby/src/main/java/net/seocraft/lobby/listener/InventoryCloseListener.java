package net.seocraft.lobby.listener;

import com.google.inject.Inject;
import net.seocraft.lobby.Lobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @Inject private Lobby lobby;

    @EventHandler
    public void inventoryCloseListener(InventoryCloseEvent event) {
        this.lobby.getLobbyMenuClose().remove(event.getPlayer());
    }
}
