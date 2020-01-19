package net.seocraft.commons.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class InventoryUtils {

    public static Inventory createInventory(String title, Integer number, Map<Integer, ItemStack> items) {
        Inventory inventory = Bukkit.getServer().createInventory(
                null, number,
                ChatColor.DARK_GRAY + title
        );
        items.forEach(inventory::setItem);
        return inventory;
    }

}
