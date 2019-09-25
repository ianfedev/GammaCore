package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.SpectatorManager;
import net.seocraft.api.core.user.User;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CraftSpectatorManager implements SpectatorManager {

    @Inject private CoreGameManagement coreGameManagement;

    @Override
    public void enableSpectatorMode(@NotNull User user, @NotNull Player player) {

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFlying(true);
        player.setGameMode(GameMode.ADVENTURE);
        this.coreGameManagement.addSpectatingPlayer(player);

        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (!this.coreGameManagement.getSpectatingPlayers().contains(onlinePlayer)) {
                onlinePlayer.hidePlayer(player);
            } else {
                // TODO: Ghost mode
            }
        });

        // TODO: Give toolbar to spectators
    }
}
