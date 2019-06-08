package net.seocraft.commons.bukkit.util;

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

    public static void infoAlert(@NotNull Player player, @NotNull String message) {
        player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1f, 1f);
        player.sendMessage(ChatColor.AQUA + message);
    }
}
