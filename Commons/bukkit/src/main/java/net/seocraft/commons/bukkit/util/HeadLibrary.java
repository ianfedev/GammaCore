package net.seocraft.commons.bukkit.util;

import net.seocraft.commons.bukkit.minecraft.SkinSkullHandler;
import org.bukkit.inventory.ItemStack;

public class HeadLibrary {

    public static ItemStack selectHead() {
        return SkinSkullHandler.getSkull("http://textures.minecraft.net/texture/b1dd4fe4a429abd665dfdb3e21321d6efa6a6b5e7b956db9c5d59c9efab25");
    }

    public static ItemStack spanishHead() {
        return SkinSkullHandler.getSkull("http://textures.minecraft.net/texture/a9f5799dfb7de65350337e735651d4c831f1c2a827d584b02d8e875ff8eaa2");
    }

    public static ItemStack englishHead() {
        return SkinSkullHandler.getSkull("http://textures.minecraft.net/texture/4cac9774da1217248532ce147f7831f67a12fdcca1cf0cb4b3848de6bc94b4");
    }

    public static ItemStack frenchHead() {
        return SkinSkullHandler.getSkull("http://textures.minecraft.net/texture/9b3495e9dbd5a426e1446e6627bf8dd55d9612ce3b55a8596e112b28db9ea3a");
    }
}
