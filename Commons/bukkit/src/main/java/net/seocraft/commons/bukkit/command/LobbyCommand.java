package net.seocraft.commons.bukkit.command;

import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import java.util.logging.Level;

public class LobbyCommand implements CommandClass {


    @Command(names = {"lobby", "l", "hub"})
    public boolean mainCommand(CommandSender commandSender) {
        Bukkit.getLogger().log(Level.INFO, "Successfuckly");
        return true;
    }
}
