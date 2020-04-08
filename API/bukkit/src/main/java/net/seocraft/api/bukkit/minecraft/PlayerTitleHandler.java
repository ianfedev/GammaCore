package net.seocraft.api.bukkit.minecraft;

import org.bukkit.entity.Player;

public class PlayerTitleHandler {

    public static void sendTitle(Player player, String title, String subTitle) {
        player.sendTitle(title, subTitle);
    }
}
