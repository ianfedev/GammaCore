package net.seocraft.commons.bukkit.utils;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class ChatAlertLibrary {

    public static void errorChatAlert(Player player, @Nullable String message) {
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1f);
        player.sendMessage(ChatColor.RED + message);
    }

    public static void infoAlert(Player player, @Nullable String message) {
        player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1f, 1f);
        player.sendMessage(ChatColor.AQUA + message);
    }
}
