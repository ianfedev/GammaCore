package net.seocraft.commons.bukkit.authentication;

import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.HeadLibrary;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class AuthenticationHeadHandler {

    static ItemStack getLanguageHead() {
        ItemStack languageBase = NBTTagHandler.addString(
                HeadLibrary.selectHead(),
                "accessor",
                "language"
        );
        ItemMeta languageMeta = languageBase.getItemMeta();
        languageMeta.setDisplayName(ChatColor.GREEN + "\u00BB " + ChatColor.GOLD + "Change your language" + ChatColor.GREEN + " \u00AB");
        languageBase.setItemMeta(languageMeta);
        return languageBase;
    }

    static Map<Integer, ItemStack> getLanguageMenu() {

        Map<Integer, ItemStack> hashMap = new HashMap<>();

        // English head (Inventory position: 3)

        ItemStack englishHead = HeadLibrary.englishHead();
        ArrayList<String> englishLore = new ArrayList<>();
        ItemMeta englishDisplay = englishHead.getItemMeta();
        englishDisplay.setDisplayName(ChatColor.AQUA + "English");
        englishLore.add(ChatColor.GRAY + "Select english language.");
        englishLore.add("");
        englishLore.add(ChatColor.YELLOW + "Click to change your language!");
        englishDisplay.setLore(englishLore);
        englishHead.setItemMeta(englishDisplay);
        hashMap.put(3, NBTTagHandler.addString(
                englishHead, "language_accessor", "en"
        ));
        
        // Spanish head (Inventory position: 4)
        
        ItemStack spanishHead = HeadLibrary.spanishHead();
        ArrayList<String> spanishLore = new ArrayList<>();
        ItemMeta spanishDisplay = spanishHead.getItemMeta();
        spanishDisplay.setDisplayName(ChatColor.AQUA + "Español");
        spanishLore.add(ChatColor.GRAY + "Selecciona el lenguaje español.");
        spanishLore.add("");
        spanishLore.add(ChatColor.YELLOW + "¡Clíc para cambiar tu lenguaje!");
        spanishDisplay.setLore(spanishLore);
        spanishHead.setItemMeta(spanishDisplay);
        hashMap.put(4, NBTTagHandler.addString(
                spanishHead, "language_accessor", "es"
        ));

        // Spanish head (Inventory position: 5)

        ItemStack frenchHead = HeadLibrary.frenchHead();
        ArrayList<String> frenchLore = new ArrayList<>();
        ItemMeta frenchDisplay = frenchHead.getItemMeta();
        frenchDisplay.setDisplayName(ChatColor.AQUA + "Français");
        frenchLore.add(ChatColor.GRAY + "Sélectionnez la langue française.");
        frenchLore.add("");
        frenchLore.add(ChatColor.YELLOW + "Cliquez pour changer votre langue!");
        frenchDisplay.setLore(frenchLore);
        frenchHead.setItemMeta(frenchDisplay);
        hashMap.put(5, NBTTagHandler.addString(
                frenchHead, "language_accessor", "fr"
        ));

        return hashMap;
    }
}
