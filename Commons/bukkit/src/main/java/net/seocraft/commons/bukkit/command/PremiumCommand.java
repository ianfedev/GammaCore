package net.seocraft.commons.bukkit.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import net.seocraft.api.bukkit.session.PremiumStatusManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class PremiumCommand implements CommandClass {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private PremiumStatusManager premiumStatusManager;
    @Inject private TranslatableField translatableField;

    @Command(names = {"premium"})
    public boolean mainCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();
                    if (this.premiumStatusManager.canEnablePremium(user))
                        ChatAlertLibrary.errorChatAlert(
                                player,
                                this.translatableField.getUnspacedField(user.getLanguage(), "commons_premium_validation_error")
                        );

                    try {
                        boolean premium = this.premiumStatusManager.togglePremiumStatus(user);
                        if (premium) {
                            ChatAlertLibrary.infoAlert(
                                    player,
                                    this.translatableField.getUnspacedField(user.getLanguage(), "commons_premium_validation_success")
                            );
                        } else {
                            ChatAlertLibrary.infoAlert(
                                    player,
                                    this.translatableField.getUnspacedField(user.getLanguage(), "commons_premium_validation_disabled")
                            );
                        }
                    } catch (Unauthorized | JsonProcessingException | BadRequest | NotFound | InternalServerError ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "[Commons] There was an error updating premium status", ex);
                        ChatAlertLibrary.errorChatAlert(player);
                    }
                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            });
        }
        return true;
    }

}
