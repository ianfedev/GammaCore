package net.seocraft.lobby.hiding;

import com.google.inject.Inject;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.bukkit.lobby.HidingGadgetManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HidingGadgetListener implements Listener {

    @Inject private HidingGadgetManager hidingGadgetManager;

    @EventHandler
    public void hideGadgetClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack clickedItem = player.getItemInHand();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Detect if element has designed tag
            if (NBTTagHandler.hasString(clickedItem, "accessor") &&
                    (
                            NBTTagHandler.getString(clickedItem, "accessor").equalsIgnoreCase("show_players") ||
                            NBTTagHandler.getString(clickedItem, "accessor").equalsIgnoreCase("hide_players")
                    )
            ) {
                String accessor = NBTTagHandler.getString(clickedItem, "accessor");
                if (accessor.equalsIgnoreCase("show_players")) {
                    this.hidingGadgetManager.disableHiding(player);
                } else if (accessor.equalsIgnoreCase("hide_players")) {
                    this.hidingGadgetManager.enableHiding(player);
                }
                event.setCancelled(true);
            }
        }
    }
}
