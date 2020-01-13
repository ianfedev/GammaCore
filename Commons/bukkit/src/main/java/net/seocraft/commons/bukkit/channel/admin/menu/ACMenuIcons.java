package net.seocraft.commons.bukkit.channel.admin.menu;

import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.LoreDisplayArray;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

class ACMenuIcons {

    static @NotNull ItemStack getChatIcon(@NotNull TranslatableField field, @NotNull User user) {

        // --- Creation and display --- //
        ItemStack chat = NBTTagHandler.addString(
                new ItemStack(Material.BOOK_AND_QUILL, 1),
                "ac_icon",
                "chat"
        );
        ItemMeta chatMeta = chat.getItemMeta();
        chatMeta.setDisplayName(ChatColor.GREEN + field.getUnspacedField(user.getLanguage(), "commons_ac_menu_chat"));
        if (!user.hasAdminChatActive())
            chatMeta.setDisplayName(ChatColor.RED + field.getUnspacedField(user.getLanguage(), "commons_ac_menu_chat"));


        // --- Lore concatenation --- //
        LoreDisplayArray<String> loreArray = new LoreDisplayArray<>();
        loreArray.add(
                field.getUnspacedField(user.getLanguage(), "commons_ac_menu_chat_lore") + ".",
                ChatColor.GRAY
        );
        loreArray.add(" ");
        String status = field.getUnspacedField(user.getLanguage(), "commons_status") + ": ";
        if (!user.hasAdminChatActive()) status = status + ChatColor.RED + field.getUnspacedField(user.getLanguage(), "commons_disabled").toUpperCase();
        if (user.hasAdminChatActive()) status = status + ChatColor.GREEN + field.getUnspacedField(user.getLanguage(), "commons_enabled").toUpperCase();
        loreArray.add(status);
        chatMeta.setLore(loreArray);
        chat.setItemMeta(chatMeta);
        return chat;
    }

    static @NotNull ItemStack getLogsIcon(@NotNull TranslatableField field, @NotNull User user) {

        // --- Creation and display --- //
        ItemStack log = NBTTagHandler.addString(
                new ItemStack(Material.SPRUCE_DOOR_ITEM, 1),
                "ac_icon",
                "log"
        );
        ItemMeta logMeta = log.getItemMeta();
        logMeta.setDisplayName(ChatColor.GREEN + field.getUnspacedField(user.getLanguage(), "commons_ac_menu_logs"));
        if (!user.hasAdminChatActive())
            logMeta.setDisplayName(ChatColor.RED + field.getUnspacedField(user.getLanguage(), "commons_ac_menu_logs"));

        // --- Lore concatenation --- //
        LoreDisplayArray<String> logArray = new LoreDisplayArray<>();
        logArray.add(
                field.getUnspacedField(user.getLanguage(), "commons_ac_menu_logs_lore") + ".",
                ChatColor.GRAY
        );
        logArray.add(" ");
        String statusLog = field.getUnspacedField(user.getLanguage(), "commons_status") + ": ";
        //TODO: Change for real option
        if (!user.hasAdminChatActive()) statusLog = statusLog + ChatColor.RED + field.getUnspacedField(user.getLanguage(), "commons_disabled").toUpperCase();
        if (user.hasAdminChatActive()) statusLog = statusLog + ChatColor.GREEN + field.getUnspacedField(user.getLanguage(), "commons_enabled").toUpperCase();
        logArray.add(statusLog);
        logMeta.setLore(logArray);
        log.setItemMeta(logMeta);
        return log;
    }

    static @NotNull ItemStack getPunishmentIcon(@NotNull TranslatableField field, @NotNull User user) {
        ItemStack punishment = NBTTagHandler.addString(
                new ItemStack(Material.WOOD_SWORD, 1),
                "ac_icon",
                "punishment"
        );
        ItemMeta punishmentMeta = punishment.getItemMeta();
        punishmentMeta.setDisplayName(ChatColor.GREEN + field.getUnspacedField(user.getLanguage(), "commons_ac_menu_punishment"));
        if (!user.hasAdminChatActive())
            punishmentMeta.setDisplayName(ChatColor.RED + field.getUnspacedField(user.getLanguage(), "commons_ac_menu_punishment"));

        LoreDisplayArray<String> punishmentLore = new LoreDisplayArray<>();
        punishmentLore.add(
                field.getUnspacedField(user.getLanguage(), "commons_ac_menu_punishment_lore") + ".",
                ChatColor.GRAY
        );
        punishmentLore.add(" ");
        String statusPunishment = field.getUnspacedField(user.getLanguage(), "commons_status") + ": ";
        //TODO: Change for real option
        if (!user.hasAdminChatActive()) statusPunishment =statusPunishment + ChatColor.RED + field.getUnspacedField(user.getLanguage(), "commons_disabled").toUpperCase();
        if (user.hasAdminChatActive()) statusPunishment = statusPunishment + ChatColor.GREEN + field.getUnspacedField(user.getLanguage(), "commons_enabled").toUpperCase();
        punishmentLore.add(statusPunishment);
        punishmentMeta.setLore(punishmentLore);
        punishment.setItemMeta(punishmentMeta);
        return punishment;
    }

    static @NotNull ItemStack getDivider() {
        return NBTTagHandler.addString(
                new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15),
                "ac_icon",
                "divider"
        );
    }
}
