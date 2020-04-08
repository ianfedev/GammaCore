package net.seocraft.api.bukkit.minecraft;

import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;

public class NBTTagHandler {

    @NotNull
    private static NBTTagCompound getTag(@NotNull org.bukkit.inventory.ItemStack item) {
        ItemStack itemNms = CraftItemStack.asNMSCopy(item);

        return itemNms.hasTag() ? itemNms.getTag() : new NBTTagCompound();
    }

    @NotNull
    private static org.bukkit.inventory.ItemStack setTag(@NotNull org.bukkit.inventory.ItemStack item, NBTTagCompound tag) {
        ItemStack itemNms = CraftItemStack.asNMSCopy(item);
        itemNms.setTag(tag);
        return CraftItemStack.asBukkitCopy(itemNms);
    }

    @NotNull
    public static org.bukkit.inventory.ItemStack addString(@NotNull org.bukkit.inventory.ItemStack item, String name, String value) {
        NBTTagCompound tag = getTag(item);
        tag.setString(name, value);
        return setTag(item, tag);
    }

    public static boolean hasString(@NotNull org.bukkit.inventory.ItemStack item, String name) {
        NBTTagCompound tag = getTag(item);
        return tag.hasKey(name);
    }

    public static String getString(@NotNull org.bukkit.inventory.ItemStack item, String name) {
        NBTTagCompound tag = getTag(item);
        return tag.getString(name);
    }

}
