package net.seocraft.lobby.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Parameter;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.MatchFinder;
import net.seocraft.api.bukkit.stats.Stats;
import net.seocraft.api.bukkit.stats.StatsProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import net.seocraft.api.core.user.UserStorageProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class TeleportCommand implements CommandClass {

    @Inject private TeleportManager teleportManager;
    @Inject private StatsProvider statsProvider;
    @Inject private UserStorageProvider userStorageProvider;

    @Command(names = {"tp", "teleport", "tele"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tp")
    public boolean teleportCommand(CommandSender commandSender, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportManager.playerTeleport(player, targetPlayer, silent);
        }
        return true;
    }

    @Command(names = {"tphere", "teleporthere", "th"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tphere")
    public boolean teleportHereCommand(CommandSender commandSender, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportManager.playerTeleportOwn(player, targetPlayer, silent);
        }
        return true;
    }

    @Command(names = {"tpall", "teleportall", "ta"}, usage = "/<command> [-s]", permission = "commons.staff.lobby.tpall")
    public boolean teleportAllCommand(CommandSender commandSender, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            this.teleportManager.playerTeleportAll(player, silent);
        }
        return true;
    }

    @Command(names = {"spawn"}, usage = "/<command> [target] [-s]", permission = "commons.staff.lobby.spawn")
    public boolean spawnCommand(CommandSender commandSender, CommandContext context, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (context.getArgumentsLength() == 1) {
                this.teleportManager.spawnTeleport(player, target, silent);
            } else {
                this.teleportManager.spawnTeleport(player, null, silent);
            }
        }
        return true;
    }

    @Command(names = {"test"})
    public boolean testCommand(CommandSender sender) {
        try {

            Stats playerStats = this.statsProvider.getPlayerStats(this.userStorageProvider.findUserByIdSync("5d8e8bf11af627456703dd93"));
            playerStats.getSkyWarsStats().addCoins(15);
            this.statsProvider.updatePlayerStats(playerStats);
        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException unauthorized) {
            unauthorized.printStackTrace();
        }
        return true;
    }


}
