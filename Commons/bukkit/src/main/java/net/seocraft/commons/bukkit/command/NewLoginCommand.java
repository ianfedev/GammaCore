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

public class NewLoginCommand implements CommandClass {

    @Inject
    private UserLoginManagement userLoginManagement;

    @ACommand(names = {"login", "l"}, desc = "Command used to authenticate old users")
    public boolean loginCommand(@Injected(true) @Sender Player sender, String password) {
        try {
            this.userLoginManagement.loginUser(sender, password);
        } catch (IOException e) {
            ChatAlertLibrary.errorChatAlert(sender);
            e.printStackTrace();
        }
        return true;
    }
}
