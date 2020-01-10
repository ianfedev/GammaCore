package net.seocraft.commons.bukkit.authentication;

import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class AuthenticationInventoryDropListener implements Listener {

    @EventHandler
    public void inventoryDropEvent(PlayerDropItemEvent event) {
        if (NBTTagHandler.hasString(event.getItemDrop().getItemStack(), "accessor")) {
            event.setCancelled(true);
        }
    }
}
