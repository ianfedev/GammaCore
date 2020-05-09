package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.seocraft.api.bukkit.user.UserLoginManagement;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import org.bukkit.entity.Player;

import java.io.IOException;

public class NewRegisterCommand implements CommandClass {

    @Inject
    private UserLoginManagement userLoginManagement;

    @ACommand(names = {"register", "registro"}, desc = "Command used to register new users at the network")
    public boolean loginCommand(@Injected(true) @Sender Player sender, String password) {
        try {
            this.userLoginManagement.registerUser(sender, password);
        } catch (IOException e) {
            ChatAlertLibrary.errorChatAlert(sender);
            e.printStackTrace();
        }
        return true;
    }
}
