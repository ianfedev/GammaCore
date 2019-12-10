package net.seocraft.commons.bukkit.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.CommandContext;
import net.seocraft.api.bukkit.user.UserLoginManagement;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

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