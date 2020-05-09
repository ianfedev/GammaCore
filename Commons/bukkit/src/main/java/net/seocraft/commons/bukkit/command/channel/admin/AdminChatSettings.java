package net.seocraft.commons.bukkit.command.channel.admin;

import com.google.inject.Inject;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import net.seocraft.api.bukkit.channel.admin.menu.ACMenuDisplay;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminChatSettings implements CommandClass {

    @Inject private ACMenuDisplay acMenuDisplay;

    @ACommand(names = {"acs"}, permission = "commons.staff.chat")
    public boolean chatSettings(CommandSender commandSender) {
        this.acMenuDisplay.openInventory((Player) commandSender);
        return true;
    }

}