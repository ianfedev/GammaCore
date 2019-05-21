package net.seocraft.commons.bukkit.commands;

import com.google.inject.Inject;
import me.ggamer55.bcm.parametric.CommandClass;
import me.ggamer55.bcm.parametric.annotation.Command;
import me.ggamer55.bcm.parametric.annotation.JoinedString;
import net.seocraft.api.bukkit.user.UserStore;
import net.seocraft.commons.bukkit.whisper.WhisperManager;
import net.seocraft.commons.bukkit.whisper.WhisperResponse;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.commons.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class WhisperCommand implements CommandClass {

    @Inject private WhisperManager whisperManager;
    @Inject private UserStore userStore;
    @Inject private TranslatableField translator;

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
                        ChatAlertLibrary.infoAlert(sender,
                                this.translator.getUnspacedField(
                                        userSender.getLanguage(),
                                        "commons_message_nulled"
                                ));
                        return;
                    }

                    if (response.getResponse() == WhisperResponse.Response.PLAYER_OFFLINE) {
                        ChatAlertLibrary.infoAlert(sender,
                                this.translator.getUnspacedField(
                                        userSender.getLanguage(),
                                        "commons_player_offline"
                                ));
                        return;
                    }

                    if (response.getResponse() == WhisperResponse.Response.ERROR) {
                        ChatAlertLibrary.errorChatAlert(sender,
                                this.translator.getUnspacedField(
                                        userSender.getLanguage(),
                                        "commons_system_error"
                                ));
                        Bukkit.getLogger().log(Level.SEVERE, "An error ocurred while executing the whisper command", response.getThrowedException());
                        return;
                    }
                });
            });
        });
        return true;
    }
}