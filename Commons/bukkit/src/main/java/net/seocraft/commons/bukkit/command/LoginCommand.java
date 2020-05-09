package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.*;
import me.fixeddev.ebcm.part.ArgumentPart;
import net.seocraft.api.bukkit.user.UserLoginManagement;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public class LoginCommand implements CommandAction {

    @Inject private UserLoginManagement userLoginManagement;

    public @NotNull Command getCommand() {
        return ImmutableCommand.builder(CommandData.builder("login"))
                .addPart(ArgumentPart.builder("password", String.class).setRequired(true).build())
                .setAction(this)
                .build();
    }

    @Override
    public boolean execute(CommandContext commandContext) {
        CommandSender sender = commandContext.getObject(CommandSender.class, "SENDER");
        Optional<String> password = commandContext.getValue(commandContext.getParts("password").get(0));
        if (password.isPresent()) {
            try {
                this.userLoginManagement.loginUser((Player) sender, password.get());
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert((Player) sender);
                e.printStackTrace();
            }
        }
        return true;
    }
}