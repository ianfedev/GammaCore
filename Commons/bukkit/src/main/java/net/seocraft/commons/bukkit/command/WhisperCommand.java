package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.ConsumedArgs;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import me.fixeddev.ebcm.parametric.annotation.Named;
import net.seocraft.api.bukkit.whisper.WhisperManager;
import net.seocraft.api.bukkit.whisper.WhisperResponse;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class WhisperCommand implements CommandClass {

    @Inject private WhisperManager whisperManager;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translator;

    private Channel<String> messager;

    @ACommand(names = {"msg", "whisper", "tell", "w", "m", "t"})
    public boolean whisperCommand(@Injected(true) @Named("SENDER")CommandSender commandSender, @Named("target") OfflinePlayer target, @ConsumedArgs(0) String message) {

        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;

            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(sender.getDatabaseIdentifier()), asyncUserSender -> {

                if (asyncUserSender.getStatus() != AsyncResponse.Status.SUCCESS) {
                    ChatAlertLibrary.errorChatAlert(sender);
                    return;
                }

                User userSender = asyncUserSender.getResponse();

                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(((Player) target).getDatabaseIdentifier()), asyncTargetUser -> {
                    if (asyncTargetUser.getStatus() == AsyncResponse.Status.SUCCESS) {
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
                    } else {
                        ChatAlertLibrary.errorChatAlert(sender);
                    }
                });
            });
        }
        return true;
    }

    private @NotNull String getPlayerIP(@NotNull Player player) {
        return player.getAddress().toString().split(":")[0].replace("/", "");
    }

    @ACommand(names = {"testMessager"})
    public boolean testMessager() {
        messager.sendMessage("test");
        Bukkit.broadcastMessage("Send message test");
        return true;
    }

}