package net.seocraft.lobby.hotbar;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.selector.LobbySelectorMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public class GameSelectorListener implements Listener  {

    @Inject private CloudManager cloudManager;

    @EventHandler
    public void lobbySelectorListener(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        HumanEntity entity = event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if ((entity instanceof Player)) {
            Player player = (Player) entity;
            if (event.getClick().equals(ClickType.LEFT)) {
                if (NBTTagHandler.hasString(clickedItem, "game_selector_opt")) {
                    this.cloudManager.sendPlayerToGroup(player, NBTTagHandler.getString(clickedItem, "game_selector_opt"));
                }
            } else {
                event.setCancelled(true);
            }
        }

    }

}
