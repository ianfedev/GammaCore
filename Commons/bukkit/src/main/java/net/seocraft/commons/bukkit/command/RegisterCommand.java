package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.CommandContext;
import net.seocraft.api.bukkit.user.UserLoginManagement;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterCommand extends AbstractAdvancedCommand {

    @Inject private UserLoginManagement userLoginManagement;

    public RegisterCommand() {
        super(
                new String[]{"register", "registro"},
                "/<command> <password>",
                "Command used to register new users at the network",
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
            this.userLoginManagement.registerUser(player, commandContext.getArgument(0));
        } catch (IOException e) {
            ChatAlertLibrary.errorChatAlert(player);
        }
        return true;
    }
}