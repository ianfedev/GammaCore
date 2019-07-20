package net.seocraft.lobby.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.commons.core.backend.http.AsyncResponse;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.old.util.ChatAlertLibrary;
import net.seocraft.lobby.hiding.HidingGadgetHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HidingGadgetCommand implements CommandClass {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private HidingGadgetHandler hidingGadgetHandler;
    @Inject private GameSessionManager gameSessionManager;

    @Command(names = {"hideplayers", "hidep", "hp"})
    public boolean mainCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession = this.gameSessionManager.getCachedSession(player.getName());
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
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
