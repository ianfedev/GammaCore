package net.seocraft.commons.bukkit.game.management.menu;

import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.bukkit.utils.LoreDisplayArray;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectatorToolbar {

    public static ItemStack getLobbyReturnItem(String l, TranslatableField field) {
        ItemStack returnBase = NBTTagHandler.addString(
                new ItemStack(Material.NETHER_STAR, 1),
                "hotbar_accessor",
                "back_lobby"
        );

        ItemMeta meta = returnBase.getItemMeta();
        meta.setDisplayName(
                ChatColor.RED + "" + ChatColor.BOLD +
                        field.getField(l, "commons_spectator_back") +
                ChatColor.GRAY
                        + "(" + field.getUnspacedField(l, "commons_right_click") + ")"
        );

        LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();
        loreDisplayArray.add(
                field.getUnspacedField(l, "commons_spectator_back_lore"),
                ChatColor.GRAY
        );

        meta.setLore(loreDisplayArray);

        returnBase.setItemMeta(meta);
        return returnBase;
    }

    public static ItemStack getPlayAgainItem(String l, TranslatableField field, boolean manual) {
        ItemStack playBase = NBTTagHandler.addString(
                new ItemStack(Material.BOOK, 1),
                "hotbar_accessor",
                "replay"
        );

        ItemMeta meta = playBase.getItemMeta();
        String display = "commons_spectator_playagain";
        String lore = "commons_spectator_playagain_lore";
        if (manual) {
            display = "commons_spectator_play_spect";
            lore = "commons_spectator_play_spect_lore";
        }
        meta.setDisplayName(
                ChatColor.RED + "" + ChatColor.BOLD +
                        field.getField(l, display) +
                        ChatColor.GRAY
                        + "(" + field.getUnspacedField(l, "commons_right_click") + ")"
        );

        LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();
        loreDisplayArray.add(
                field.getUnspacedField(l, lore),
                ChatColor.GRAY
        );

        meta.setLore(loreDisplayArray);

        playBase.setItemMeta(meta);
        return playBase;
    }
}
