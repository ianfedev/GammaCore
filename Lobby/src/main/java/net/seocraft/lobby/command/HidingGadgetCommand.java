package net.seocraft.lobby.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.lobby.hiding.HidingGadgetHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HidingGadgetCommand implements CommandClass {

    @Inject private UserStoreHandler userStoreHandler;
    @Inject private HidingGadgetHandler hidingGadgetHandler;
    @Inject private SessionHandler sessionHandler;

    @Command(names = {"hideplayers", "hidep", "hp"})
    public boolean mainCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();
                    if (user.isHiding()) {
                        this.hidingGadgetHandler.disableHiding(player);
                    } else {
                        this.hidingGadgetHandler.enableHiding(player);
                    }
                } else {
                    ChatAlertLibrary.errorChatAlert(player, null);
                }
            });
        }
        return true;
    }
}
