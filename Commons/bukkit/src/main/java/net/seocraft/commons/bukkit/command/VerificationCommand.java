package net.seocraft.commons.bukkit.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.core.backend.user.UserMailVerification;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VerificationCommand implements CommandClass {

    @Inject
    private UserStorageProvider userStorageProvider;
    @Inject
    private TranslatableField translatableField;
    @Inject
    private UserMailVerification userMailVerification;
    @Inject
    private ServerTokenQuery serverTokenQuery;
    @Inject
    private ObjectMapper mapper;

    @ACommand(names = {"verify", "linkaccount", "verificar", "link"})
    public boolean mainCommand(@Injected(true) @Sender Player player, String mail) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
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
                        ObjectNode node = mapper.createObjectNode();
                        node.put("user", user.getId());
                        node.put("email", mail);

                        String test = this.userMailVerification.executeRequest(
                                this.mapper.writeValueAsString(node),
                                this.serverTokenQuery.getToken()
                        );
                        ChatAlertLibrary.infoAlert(
                                player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_verify_sent"
                                ).replace("%%mail%%", ChatColor.YELLOW + mail + ChatColor.AQUA)
                        );

                    } catch (BadRequest | InternalServerError | JsonProcessingException | NotFound unauthorized) {
                        ChatAlertLibrary.errorChatAlert(
                                player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_verify_error_request"
                                )
                        );
                        unauthorized.printStackTrace();
                    } catch (Unauthorized ex) {
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
        return true;
    }

}