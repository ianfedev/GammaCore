package net.seocraft.lobby.hotbar;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public class GameSelectorListener implements Listener {

    @Inject
    private CloudManager cloudManager;

    @EventHandler
    public void lobbySelectorListener(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta())
            return;
        HumanEntity entity = event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) {
            return;
        }

        if ((entity instanceof Player)) {
            Player player = (Player) entity;
            if (NBTTagHandler.hasString(clickedItem, "game_selector_opt")) {
                if (event.getClick().equals(ClickType.LEFT)) {
                    this.cloudManager.sendPlayerToGroup(player, NBTTagHandler.getString(clickedItem, "game_selector_opt"));
                }
                event.setCancelled(true);
            }
        }

    }

}
