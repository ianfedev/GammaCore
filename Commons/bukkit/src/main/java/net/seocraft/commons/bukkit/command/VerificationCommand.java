package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.backend.user.UserMailVerification;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class VerificationCommand implements CommandClass {

    @Inject private GameSessionManager gameSessionManager;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;
    @Inject private UserMailVerification userMailVerification;
    @Inject private ServerTokenQuery serverTokenQuery;

    @Command(names = {"verify", "verificar", "link"}, usage = "/<command> <mail>")
    public boolean mainCommand(CommandSender commandSender, String mail) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                if (playerSession != null) {
                    CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                        if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User user = userAsyncResponse.getResponse();
                            String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

                            if (user.isVerified()) {
                                ChatAlertLibrary.errorChatAlert(
                                        player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "\n" +
                                                        "commons_verify_already"
                                        )
                                );
                                return;
                            }

                            if (mail.matches(regex)) {
                                try {
                                    this.userMailVerification.executeRequest(user.getId(), this.serverTokenQuery.getToken());

                                    ChatAlertLibrary.infoAlert(
                                            player,
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_verify_sent"
                                            ).replace("%%mail%%", ChatColor.YELLOW + mail + ChatColor.AQUA)
                                    );

                                } catch (Unauthorized | InternalServerError | NotFound unauthorized) {
                                    ChatAlertLibrary.errorChatAlert(
                                            player,
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_verify_error_request"
                                            )
                                    );
                                } catch (BadRequest badRequest) {
                                    ChatAlertLibrary.errorChatAlert(
                                            player,
                                            this.translatableField.getUnspacedField(
                                                    user.getLanguage(),
                                                    "commons_verify_already_sent"
                                            )
                                    );
                                }
                            } else {
                                ChatAlertLibrary.errorChatAlert(
                                        player,
                                        this.translatableField.getUnspacedField(
                                                user.getLanguage(),
                                                "commons_verify_error"
                                        ).replace("%%pattern%%", "mail@domain.com")
                                );
                            }

                        } else {
                            ChatAlertLibrary.errorChatAlert(player);
                        }
                    });
                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player);
            }
        }
        return true;
    }

}
