package net.seocraft.lobby.command;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandClass {


    @Inject private TeleportManager teleportManager;

    @ACommand(names = {"tp", "teleport", "tele"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tp")
    public boolean teleportCommand(CommandSender commandSender, OfflinePlayer target, @Flag('s') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportManager.playerTeleport(player, targetPlayer, silent);
        }
        return true;
    }

    @ACommand(names = {"tphere", "teleporthere", "th"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tphere")
    public boolean teleportHereCommand(CommandSender commandSender, OfflinePlayer target, @Flag('s') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportManager.playerTeleportOwn(player, targetPlayer, silent);
        }
        return true;
    }

    @ACommand(names = {"tpall", "teleportall", "ta"}, usage = "/<command> [-s]", permission = "commons.staff.lobby.tpall")
    public boolean teleportAllCommand(CommandSender commandSender, @Flag('s') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            this.teleportManager.playerTeleportAll(player, silent);
        }
        return true;
    }

    @ACommand(names = {"spawn"}, max = 2, usage = "/<command> [target] [-s]", permission = "commons.staff.lobby.spawn")
    public boolean spawnCommand(CommandSender commandSender, CommandContext context, @Optional OfflinePlayer target, @Flag('s') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (context.getArguments().size() == 1) {
                this.teleportManager.spawnTeleport(player, target, silent);
            } else {
                this.teleportManager.spawnTeleport(player, null, silent);
            }
        }
        return true;
    }

}
