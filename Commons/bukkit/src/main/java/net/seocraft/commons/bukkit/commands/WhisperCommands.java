package net.seocraft.commons.bukkit.commands;

import com.google.inject.Inject;
import me.ggamer55.bcm.parametric.CommandClass;
import me.ggamer55.bcm.parametric.annotation.Command;
import me.ggamer55.bcm.parametric.annotation.JoinedString;
import net.seocraft.api.bukkit.user.UserStore;
import net.seocraft.api.bukkit.whisper.WhisperManager;
import net.seocraft.api.bukkit.whisper.WhisperResponse;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.commons.bukkit.utils.ChatAlertLibrary;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class WhisperCommands implements CommandClass {

    @Inject
    private WhisperManager whisperManager;
    @Inject
    private UserStore userStore;

    @Command(names = {"msg", "whisper", "tell", "w", "m", "t"}, min = 2, usage = "/<command> <target> <message...>")
    public boolean whisperCommand(CommandSender commandSender, OfflinePlayer target, @JoinedString String message) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player sender = (Player) commandSender;


        CallbackWrapper.addCallback(userStore.getUserObject(sender.getUniqueId()), userSender -> {
            CallbackWrapper.addCallback(userStore.getUserObject(target.getUniqueId()), targetUser -> {
                CallbackWrapper.addCallback(whisperManager.sendMessage(userSender, targetUser, message), response -> {
                    if (response.getResponse() == null) {
                        ChatAlertLibrary.infoAlert(sender, "Has nulleado el sistema, que pedo contigo"); // TODO: FIX THIS SHIT MAN
                        return;
                    }

                    if (response.getResponse() == WhisperResponse.Response.PLAYER_OFFLINE) {
                        ChatAlertLibrary.infoAlert(sender, "The player " + target.getName() + " is offline."); // TODO: FIX THIS SHIT MAN
                        return;
                    }

                    if (response.getResponse() == WhisperResponse.Response.ERROR) {
                        ChatAlertLibrary.errorChatAlert(sender, "An error ocurred contact an administrator"); // TODO: FIX THIS SHIT MAN
                        Bukkit.getLogger().log(Level.SEVERE, "An error ocurred while executing the whisper command", response.getThrowedException());

                        return;
                    }
                });
            });
        });
        return true;
    }
}
