package net.seocraft.lobby.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Parameter;
import net.seocraft.api.shared.serialization.model.ModelDeserializer;
import net.seocraft.api.shared.serialization.model.ModelSerializer;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.lobby.teleport.TeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandClass {

    @Inject private TeleportHandler teleportHandler;
    @Inject private SessionHandler sessionHandler;

    @Command(names = {"tp", "teleport", "tele"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tp")
    public boolean teleportCommand(CommandSender commandSender, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportHandler.playerTeleport(player, targetPlayer, silent);
        }
        return true;
    }

    @Command(names = {"tphere", "teleporthere", "th"}, usage = "/<command> <target> [-s]", permission = "commons.staff.lobby.tphere")
    public boolean teleportHereCommand(CommandSender commandSender, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player targetPlayer = Bukkit.getPlayer(target.getName());
            this.teleportHandler.playerTeleportOwn(player, targetPlayer, silent);
        }
        return true;
    }

    @Command(names = {"tpall", "teleportall", "ta"}, usage = "/<command> [-s]", permission = "commons.staff.lobby.tpall")
    public boolean teleportAllCommand(CommandSender commandSender, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            this.teleportHandler.playerTeleportAll(player, silent);
        }
        return true;
    }

    @Command(names = {"spawn"}, usage = "/<command> [target] [-s]", permission = "commons.staff.lobby.spawn")
    public boolean spawnCommand(CommandSender commandSender, CommandContext context, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (context.getArgumentsLength() == 1) {
                this.teleportHandler.spawnTeleport(player, target, silent);
            } else {
                this.teleportHandler.spawnTeleport(player, null, silent);
            }
        }
        return true;
    }

    /*public boolean testCommand() {
        GameSession gameSession = this.sessionHandler.getCachedSession("MomlessTomato");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GameSession.class, new ModelSerializer<>(GameSession.class))
                .registerTypeAdapter(GameSession.class, new ModelDeserializer<>(GameSession.class))
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        String yeison = gson.toJson(gameSession, GameSession.class);
        System.out.println(yeison);
        System.out.println(gson.fromJson(yeison, GameSession.class).getPlayerId());
        return true;
    }*/

}
