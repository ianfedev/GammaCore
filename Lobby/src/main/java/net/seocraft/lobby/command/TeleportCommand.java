package net.seocraft.lobby.command;

import com.google.inject.Inject;
import me.ggamer55.bcm.CommandContext;
import me.ggamer55.bcm.parametric.CommandClass;
import me.ggamer55.bcm.parametric.annotation.Command;
import me.ggamer55.bcm.parametric.annotation.Parameter;
import net.seocraft.lobby.teleport.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandClass {

    @Inject private TeleportHandler teleportHandler;

    @Command(names = {"tp", "teleport", "tele"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tp")
    public void teleportCommand(CommandSender commandSender, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportHandler.playerTeleport(player, targetPlayer, silent);
        }
    }

    @Command(names = {"tphere", "teleporthere", "th"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tphere")
    public void teleportHereCommand(CommandSender commandSender, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportHandler.playerTeleportOwn(player, targetPlayer, silent);
        }
    }

    @Command(names = {"tpall", "teleportall", "ta"}, usage = "/<command> [-s]", permission = "commons.staff.lobby.tpall")
    public void teleportAllCommand(CommandSender commandSender, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            this.teleportHandler.playerTeleportAll(player, silent);
        }
    }

    @Command(names = {"spawn"}, usage = "/<command> [target] [-s]", permission = "commons.staff.lobby.spawn")
    public void spawnCommand(CommandSender commandSender, CommandContext context, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (context.getArgumentsLength() == 1) {
                this.teleportHandler.spawnTeleport(player, target, silent);
            } else {
                this.teleportHandler.spawnTeleport(player, null, silent);
            }
        }
    }

}
