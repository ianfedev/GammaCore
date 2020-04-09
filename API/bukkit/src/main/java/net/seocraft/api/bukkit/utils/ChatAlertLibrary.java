package net.seocraft.api.bukkit.utils;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ChatAlertLibrary {



    public static void errorChatAlert(@NotNull Player player, @Nullable String message) {
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1f);
        if (message == null) message = "Error executing the last action, please contact an administrator.";
        player.sendMessage(ChatColor.RED + message);
    }

    public static void errorChatAlert(@NotNull Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1f);
        player.sendMessage(ChatColor.RED + "Error executing the last action, please contact an administrator.");
    }

    public static void infoAlert(@NotNull Player player, @NotNull String message) {
        player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1f, 1f);
        player.sendMessage(ChatColor.AQUA + message);
    }

    public static String transformChat(String s) {
        return s.replace("%n%", "")
        .replace("%%black%%", ChatColor.BLACK + "")
        .replace("%%dark_blue%%", ChatColor.DARK_BLUE + "")
        .replace("%%dark_green%%", ChatColor.DARK_GREEN + "")
        .replace("%%dark_aqua%%", ChatColor.DARK_AQUA + "")
        .replace("%%dark_red%%", ChatColor.DARK_RED + "")
        .replace("%%dark_purple%%", ChatColor.DARK_PURPLE + "")
        .replace("%%gold%%", ChatColor.GOLD + "")
        .replace("%%gray%%", ChatColor.GRAY + "")
        .replace("%%dark_gray%%", ChatColor.DARK_GRAY + "")
        .replace("%%blue%%", ChatColor.BLUE + "")
        .replace("%%green%%", ChatColor.GREEN + "")
        .replace("%%aqua%%", ChatColor.AQUA + "")
        .replace("%%red%%", ChatColor.RED + "")
        .replace("%%light_purple%%", ChatColor.LIGHT_PURPLE + "")
        .replace("%%yellow%%", ChatColor.YELLOW + "")
        .replace("%%white%%", ChatColor.WHITE + "")
        .replace("%%obfuscated%%", ChatColor.MAGIC + "")
        .replace("%%bold%%", ChatColor.BOLD + "")
        .replace("%%strike%%", ChatColor.STRIKETHROUGH + "")
        .replace("%%underline%%", ChatColor.UNDERLINE + "")
        .replace("%%italic%%", ChatColor.ITALIC + "")
        .replace("%%reset%%", ChatColor.RESET + "");
    }
}
