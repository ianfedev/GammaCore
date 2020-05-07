package net.seocraft.lobby.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.seocraft.api.bukkit.lobby.HidingGadgetManager;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import org.bukkit.entity.Player;

public class HidingGadgetCommand implements CommandClass {

    @Inject
    private UserStorageProvider userStorageProvider;
    @Inject
    private HidingGadgetManager hidingGadgetManager;

    @ACommand(names = {"hideplayers", "hidep", "hp"})
    public boolean mainCommand(@Injected(true) @Sender Player player) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (user.getGameSettings().getGeneral().isHidingPlayers()) {
                    this.hidingGadgetManager.disableHiding(player);
                } else {
                    this.hidingGadgetManager.enableHiding(player);
                }
            } else {
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        });

        return true;
    }
}
