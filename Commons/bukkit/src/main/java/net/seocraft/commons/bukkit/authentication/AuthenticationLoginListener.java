package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.minecraft.PlayerTitleHandler;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AuthenticationLoginListener {

    private CommonsBukkit instance = CommonsBukkit.getInstance();
    private FileConfiguration config = instance.getConfig();
    @Inject private TranslatableField translator;
    @Inject private PlayerTitleHandler titleHandler;

    public void authenticationLoginListener(Player player, Boolean registered, String language) {
        World w = Bukkit.getServer().getWorld(config.getString("authentication.coords.world"));
        int x = config.getInt("authentication.coords.x");
        int y = config.getInt("authentication.coords.y");
        int z = config.getInt("authentication.coords.z");
        int yaw = config.getInt("authentication.coords.yaw");
        int pitch = config.getInt("authentication.coords.pitch");
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
            this.titleHandler.sendTitle(player,
                    ChatColor.AQUA + this.translator.getField(language, "authentication_registered_title") + ChatColor.YELLOW + player.getName(),
                    ChatColor.YELLOW + this.translator.getField(language, "authentication_registered_sub") + ChatColor.RED + "/login <" + this.translator.getField(language, "commons_password") + ">"
            );
            this.instance.loginAttempts.put(player.getUniqueId(), 0);
        } else {
            this.titleHandler.sendTitle(player,
                    ChatColor.AQUA + this.translator.getField(language, "authentication_unregistered_title") + ChatColor.YELLOW + player.getName(),
                    ChatColor.YELLOW + this.translator.getField(language, "authentication_unregistered_sub") + ChatColor.RED + "/register <" + this.translator.getField(language, "commons_password") + ">"
            );
            this.instance.unregisteredPlayers.add(player.getUniqueId());
        }
    }

}
