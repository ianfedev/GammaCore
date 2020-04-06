package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import net.seocraft.api.bukkit.session.PremiumStatusManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

                    if (this.premiumStatusManager.togglePremiumStatus(user)) {
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

                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            });
        }
        return true;
    }

}
