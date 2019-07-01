package net.seocraft.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerBlockInteractionListener implements Listener {

    @EventHandler
    public void playerBlockPlaceEvent(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void playerBlockBreakEvent(BlockBreakEvent event) {
        event.setCancelled(true);
    }
}
