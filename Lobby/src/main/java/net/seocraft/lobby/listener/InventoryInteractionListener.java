package net.seocraft.lobby.listener;

import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryInteractionListener implements Listener {

    @EventHandler
    public void inventoryInteractionEvent(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = player.getItemInHand();
            if (NBTTagHandler.hasString(clickedItem, "accessor")) {
                event.setCancelled(true);
            }
        }
    }
}
