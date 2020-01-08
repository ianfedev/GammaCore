package net.seocraft.lobby.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import net.seocraft.api.bukkit.lobby.HidingGadgetManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HidingGadgetCommand implements CommandClass {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private HidingGadgetManager hidingGadgetManager;

    @Command(names = {"hideplayers", "hidep", "hp"})
    public boolean mainCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();
                    if (user.isHiding()) {
                        this.hidingGadgetManager.disableHiding(player);
                    } else {
                        this.hidingGadgetManager.enableHiding(player);
                    }
                } else {
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            });
        }
        return true;
    }
}
