package net.seocraft.lobby.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Flag;
import me.fixeddev.bcm.parametric.annotation.Parameter;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.MatchFinder;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.session.GameSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class TeleportCommand implements CommandClass {

    @Inject private TeleportManager teleportManager;
    @Inject private MatchFinder matchFinder;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private CloudManager cloudManager;
    @Inject private ObjectMapper mapper;
    @Inject private RedisClient client;

    @Command(names = {"tp", "teleport", "tele"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tp")
    public boolean teleportCommand(CommandSender commandSender, OfflinePlayer target, @Flag('s') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportManager.playerTeleport(player, targetPlayer, silent);
        }
        return true;
    }

    @Command(names = {"tphere", "teleporthere", "th"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tphere")
    public boolean teleportHereCommand(CommandSender commandSender, OfflinePlayer target, @Flag('s') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportManager.playerTeleportOwn(player, targetPlayer, silent);
        }
        return true;
    }

    @Command(names = {"tpall", "teleportall", "ta"}, usage = "/<command> [-s]", permission = "commons.staff.lobby.tpall")
    public boolean teleportAllCommand(CommandSender commandSender, @Flag('s') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            this.teleportManager.playerTeleportAll(player, silent);
        }
        return true;
    }

    @Command(names = {"spawn"}, usage = "/<command> [target] [-s]", permission = "commons.staff.lobby.spawn")
    public boolean spawnCommand(CommandSender commandSender, CommandContext context, OfflinePlayer target, @Flag('s') boolean silent) {
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

    @Command(names = {"test"}, usage = "/<command> <game>")
    public boolean testCommand(CommandSender sender, String name) {
        try {
            if (name.equalsIgnoreCase("games")) {
                FinderResult result = this.matchFinder.findAvailableMatch("5d5a11f35f1de46c232babae", "5d5a12c08f2258859e1ea7c9", "tnt_run", false);
                testSpectator(sender, result);
            }

            if (name.equalsIgnoreCase("tntrun")) {
                FinderResult result = this.matchFinder.findAvailableMatch("5db64666df034f2e9a3b4800", "5db645eea534664c62702cbe", "tnt_run", false);
                testSpectator(sender, result);
            }
        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException unauthorized) {
            unauthorized.printStackTrace();
        }
        return true;
    }

    private void testSpectator(CommandSender sender, FinderResult result) throws IOException {
        String finderResult = this.mapper.writeValueAsString(result);
        this.client.setString(
                "pairing:" + this.gameSessionManager.getCachedSession(sender.getName()).getPlayerId(),
                finderResult
        );
        this.cloudManager.sendPlayerToServer((Player) sender, result.getServer().getSlug());
    }

}
