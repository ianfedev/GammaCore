package net.seocraft.commons.bukkit.command.channel.admin;

import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.seocraft.api.bukkit.channel.admin.menu.ACMenuDisplay;
import org.bukkit.entity.Player;

public class AdminChatSettings implements CommandClass {

    @Inject
    private ACMenuDisplay acMenuDisplay;

    @ACommand(names = {"acs"}, permission = "commons.staff.chat")
    public boolean chatSettings(@Injected(true) @Sender Player commandSender) {
        this.acMenuDisplay.openInventory(commandSender);
        return true;
    }

}