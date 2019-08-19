package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.JoinedString;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.ChannelListener;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.bukkit.whisper.WhisperManager;
import net.seocraft.api.bukkit.whisper.WhisperResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

public class WhisperCommand implements CommandClass {

    @Inject
    private WhisperManager whisperManager;
    @Inject
    private UserStorageProvider userStorageProvider;
    @Inject
    private GameSessionManager gameSessionManager;
    @Inject
    private TranslatableField translator;
    @Inject private Messager redisMessager;

    private Channel<String> messager;

    @Inject
    public WhisperCommand(Messager messager) {
       this.messager = messager.getChannel("test", String.class);
       this.messager.registerListener(object -> Bukkit.broadcastMessage("Received message " + object));
    }

    @Command(names = {"msg", "whisper", "tell", "w", "m", "t"}, min = 2, usage = "/<command> <target> <message...>")
    public boolean whisperCommand(CommandSender commandSender, OfflinePlayer target, @JoinedString String message) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player sender = (Player) commandSender;

        try {
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(this.gameSessionManager.getCachedSession(sender.getName()).getPlayerId()), asyncUserSender -> {
                if (asyncUserSender.getStatus() != AsyncResponse.Status.SUCCESS) {
                    ChatAlertLibrary.errorChatAlert(sender, null);

                    return;
                }

                User userSender = asyncUserSender.getResponse();

                GameSession targetSession = null;
                try {
                    targetSession = this.gameSessionManager.getCachedSession(target.getName());
                } catch (IOException e) {
                    ChatAlertLibrary.errorChatAlert(sender, null);
                    return;
                }

                if (targetSession == null) {
                    ChatAlertLibrary.errorChatAlert(sender, this.translator.getUnspacedField(
                            userSender.getLanguage(),
                            "commons_not_found") + ".");
                    return;
                }

                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(targetSession.getPlayerId()), asyncTargetUser -> {
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
        } catch (IOException e) {
            ChatAlertLibrary.errorChatAlert(
                    sender
            );
        }
        return true;
    }

    @Command(names = {"testMessager"}, max = 0)
    public boolean testMessager() {
        messager.sendMessage("test");
        Bukkit.broadcastMessage("Send message test");
        return true;
    }
}