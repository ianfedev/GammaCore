package net.seocraft.commons.bukkit.channel.admin.menu;

import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

class ACMenuOptions {

    static @NotNull ItemStack getChatOption(@NotNull TranslatableField field, @NotNull User user) {
        ItemStack chatSelector;
        if (!user.hasAdminChatActive()) {
            chatSelector =  NBTTagHandler.addString(
                    new ItemStack(Material.INK_SACK, 1, (short) 8),
                    "ac_selector",
                    "chat"
            );
        } else {
            chatSelector =  NBTTagHandler.addString(
                    new ItemStack(Material.INK_SACK, 1, (short) 10),
                    "ac_selector",
                    "chat"
            );
        }
        return getSelectorMeta(field, user, chatSelector);
    }

    static @NotNull ItemStack getLogsOption(@NotNull TranslatableField field, @NotNull User user) {
        ItemStack logsSelector;
        if (!user.hasAdminChatActive()) {
            logsSelector =  NBTTagHandler.addString(
                    new ItemStack(Material.INK_SACK, 1, (short) 8),
                    "ac_selector",
                    "logs"
            );
        } else {
            logsSelector =  NBTTagHandler.addString(
                    new ItemStack(Material.INK_SACK, 1, (short) 10),
                    "ac_selector",
                    "logs"
            );
        }
        return getSelectorMeta(field, user, logsSelector);
    }

    static @NotNull ItemStack getPunishmentOption(@NotNull TranslatableField field, @NotNull User user) {
        ItemStack punishmentSelector;
        if (!user.hasAdminChatActive()) {
            punishmentSelector =  NBTTagHandler.addString(
                    new ItemStack(Material.INK_SACK, 1, (short) 8),
                    "ac_selector",
                    "punishment"
            );
        } else {
            punishmentSelector =  NBTTagHandler.addString(
                    new ItemStack(Material.INK_SACK, 1, (short) 10),
                    "ac_selector",
                    "punishment"
            );
        }
        return getSelectorMeta(field, user, punishmentSelector);
    }

    static @NotNull ItemStack getCloseOption(@NotNull TranslatableField field, @NotNull User user) {
        ItemStack close =  NBTTagHandler.addString(
                new ItemStack(Material.INK_SACK, 1, (short) 8),
                "ac_selector",
                "close"
        );
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + field.getUnspacedField(user.getLanguage(), "commons_close"));
        close.setItemMeta(closeMeta);
        return close;
    }

    private @NotNull static ItemStack getSelectorMeta(@NotNull TranslatableField field, @NotNull User user, ItemStack punishmentSelector) {
        ItemMeta punishmentSelectorMeta = punishmentSelector.getItemMeta();
        punishmentSelectorMeta.setDisplayName(ChatColor.RED + field.getUnspacedField(user.getLanguage(), "commons_ac_menu_deactivate"));
        if (!user.hasAdminChatActive()) punishmentSelectorMeta.setDisplayName(ChatColor.GREEN + field.getUnspacedField(user.getLanguage(), "commons_ac_menu_activate"));
        punishmentSelector.setItemMeta(punishmentSelectorMeta);
        return punishmentSelector;
    }
}
