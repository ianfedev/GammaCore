package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.JoinedString;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.whisper.WhisperManager;
import net.seocraft.commons.bukkit.whisper.WhisperResponse;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class WhisperCommand implements CommandClass {

    @Inject
    private WhisperManager whisperManager;
    @Inject
    private UserStoreHandler userStoreHandler;
    @Inject
    private SessionHandler sessionHandler;
    @Inject
    private TranslatableField translator;

    @Command(names = {"msg", "whisper", "tell", "w", "m", "t"}, min = 2, usage = "/<command> <target> <message...>")
    public boolean whisperCommand(CommandSender commandSender, OfflinePlayer target, @JoinedString String message) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player sender = (Player) commandSender;

        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.sessionHandler.getCachedSession(sender.getName()).getPlayerId()), asyncUserSender -> {
            if (asyncUserSender.getStatus() != AsyncResponse.Status.SUCCESS) {
                ChatAlertLibrary.errorChatAlert(sender, null);

                return;
            }

            User userSender = asyncUserSender.getResponse();

            GameSession targetSession = this.sessionHandler.getCachedSession(target.getName());

            if (targetSession == null) {
                ChatAlertLibrary.errorChatAlert(sender, this.translator.getUnspacedField(
                        userSender.getLanguage(),
                        "commons_not_found") + ".");
                return;
            }

            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(targetSession.getPlayerId()), asyncTargetUser -> {
                if (asyncTargetUser.getStatus() != AsyncResponse.Status.SUCCESS) {
                    ChatAlertLibrary.errorChatAlert(sender, null);

                    return;
                }

                User targetUser = asyncTargetUser.getResponse();
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
                    }
                });
            });
        });
        return true;
    }
}