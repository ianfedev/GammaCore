package net.seocraft.api.bukkit.minecraft;

import org.bukkit.entity.Player;

public class PlayerTitleHandler {

    public void sendTitle(Player player, String title, String subTitle) {
        player.sendTitle(title, subTitle);
    }
}
