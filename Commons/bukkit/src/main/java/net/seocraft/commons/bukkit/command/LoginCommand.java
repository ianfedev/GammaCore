package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.CommandContext;
import net.seocraft.api.bukkit.user.UserLoginManagement;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;

public class LoginCommand extends AbstractAdvancedCommand {

    @Inject private UserLoginManagement userLoginManagement;

    public LoginCommand() {
        super(
                new String[]{"login"},
                "/<command> <password>",
                "Command used to authenticate old users",
                "",
                "",
                new ArrayList<>(),
                1,
                1,
                false,
                new ArrayList<>()
        );
    }

    @Override
    public boolean execute(CommandContext commandContext) {
        Player player = (Player) commandContext.getNamespace().getObject(CommandSender.class, "sender");
        try {
            this.userLoginManagement.loginUser(player, commandContext.getArgument(0));
        } catch (IOException e) {
            ChatAlertLibrary.errorChatAlert(player);
            e.printStackTrace();
        }
        return true;
    }
}