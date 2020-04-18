package net.seocraft.commons.bukkit.listener.game.toolbar;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GameLobbyToolbarListener implements Listener {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private CloudManager cloudManager;

    @EventHandler
    public void gameClickInventoryListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = player.getItemInHand();
        if (
                (event.getAction() == Action.RIGHT_CLICK_AIR ||  event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                 handItem != null && handItem.getType() != Material.AIR && NBTTagHandler.hasString(handItem, "hotbar_accessor")
        ) {
            if (NBTTagHandler.getString(handItem, "hotbar_accessor").equalsIgnoreCase("back_lobby"))
                this.cloudManager.sendPlayerToGroup(player, this.coreGameManagement.getGamemode().getLobbyGroup());
        }
    }

}
