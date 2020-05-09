package net.seocraft.lobby.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Default;
import me.fixeddev.ebcm.parametric.annotation.Flag;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandClass {

    @Inject
    private TeleportManager teleportManager;

    @ACommand(names = {"tp", "teleport", "tele"}, permission = "commons.staff.lobby.tp")
    public boolean teleportCommand(@Injected(true) @Sender Player player, OfflinePlayer target, @Flag('s') boolean silent) {
        Player targetPlayer = Bukkit.getPlayer(target.getName());
        this.teleportManager.playerTeleport(player, targetPlayer, silent);

        return true;
    }

    @ACommand(names = {"tphere", "teleporthere", "th"}, permission = "commons.staff.lobby.tphere")
    public boolean teleportHereCommand(@Injected(true) @Sender Player player, OfflinePlayer target, @Flag('s') boolean silent) {
        Player targetPlayer = Bukkit.getPlayer(target.getName());
        this.teleportManager.playerTeleportOwn(player, targetPlayer, silent);
        return true;
    }

    @ACommand(names = {"tpall", "teleportall", "ta"}, permission = "commons.staff.lobby.tpall")
    public boolean teleportAllCommand(@Injected(true) @Sender Player player, @Flag('s') boolean silent) {
        this.teleportManager.playerTeleportAll(player, silent);
        return true;
    }

    @ACommand(names = {"spawn"}, permission = "commons.staff.lobby.spawn")
    public boolean spawnCommand(@Injected(true) @Sender Player player, @Default OfflinePlayer target, @Flag('s') boolean silent) {
        if (target != null) {
            this.teleportManager.spawnTeleport(player, target, silent);
        } else {
            this.teleportManager.spawnTeleport(player, null, silent);
        }

        return true;
    }

}