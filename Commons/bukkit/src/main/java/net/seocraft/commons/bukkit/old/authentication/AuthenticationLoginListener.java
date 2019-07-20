package net.seocraft.commons.bukkit.old.authentication;

import com.google.inject.Inject;
import net.seocraft.commons.bukkit.minecraft.PlayerTitleHandler;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class AuthenticationLoginListener {

    @Inject private CommonsBukkit instance;
    @Inject private TranslatableField translator;

    public void authenticationLoginListener(Player player, Boolean registered, String language) {
        World w = Bukkit.getServer().getWorld(this.instance.getConfig().getString("authentication.coords.world"));
        int x = this.instance.getConfig().getInt("authentication.coords.x");
        int y = this.instance.getConfig().getInt("authentication.coords.y");
        int z = this.instance.getConfig().getInt("authentication.coords.z");
        int yaw = this.instance.getConfig().getInt("authentication.coords.yaw");
        int pitch = this.instance.getConfig().getInt("authentication.coords.pitch");
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().setHeldItemSlot(4);
        player.teleport(new Location(w, x, y, z, yaw, pitch));
        for (Player players : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(players);
        }
        player.getInventory().setItem(4, AuthenticationHeadHandler.getLanguageHead());
        if (registered) {
            PlayerTitleHandler.sendTitle(player,
                    ChatColor.AQUA + this.translator.getField(language, "authentication_registered_title") + ChatColor.YELLOW + player.getName(),
                    ChatColor.YELLOW + this.translator.getField(language, "authentication_registered_sub") + ChatColor.RED + "/login <" + this.translator.getField(language, "commons_password") + ">"
            );
            this.instance.loginAttempts.put(player.getUniqueId(), 0);
        } else {
            PlayerTitleHandler.sendTitle(player,
                    ChatColor.AQUA + this.translator.getField(language, "authentication_unregistered_title") + ChatColor.YELLOW + player.getName(),
                    ChatColor.YELLOW + this.translator.getField(language, "authentication_unregistered_sub") + ChatColor.RED + "/register <" + this.translator.getField(language, "commons_password") + ">"
            );
            this.instance.unregisteredPlayers.add(player.getUniqueId());
        }
    }

}
