package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.ggamer55.bcm.AbstractAdvancedCommand;
import me.ggamer55.bcm.CommandContext;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.serialization.TimeUtils;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.UserLoginRequest;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.authentication.AuthenticationAttemptsHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

public class LoginCommand extends AbstractAdvancedCommand {

    @Inject private CommonsBukkit instance;
    @Inject private ServerTokenQuery tokenQuery;
    @Inject private AuthenticationAttemptsHandler authenticationAttemptsHandler;
    @Inject private TranslatableField translator;
    @Inject private UserLoginRequest userLoginRequest;
    @Inject private SessionHandler sessionHandler;
    @Inject private UserStoreHandler userStoreHandler;

    public LoginCommand() {
        super(
                new String[]{"login"},
                "/<command> <password>",
                "Command used to authenticate old users",
                "",
                "",
                new ArrayList<>(),
                1,
                1,
                false,
                new ArrayList<>()
        );
    }

    @Override
    public boolean execute(CommandContext commandContext) {
        Player player = (Player) commandContext.getNamespace().getObject(CommandSender.class, "sender");
        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.sessionHandler.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (!this.instance.unregisteredPlayers.contains(player.getUniqueId())) {
                    try {
                        this.userLoginRequest.executeRequest(
                                player.getName(),
                                commandContext.getArgument(0),
                                this.tokenQuery.getToken()
                        );
                        this.instance.loginAttempts.put(
                                player.getUniqueId(),
                                this.instance.loginAttempts.get(player.getUniqueId()) + 1
                        );
                        if (user.getLastGame().equalsIgnoreCase("registrandose")) {
                            ChatAlertLibrary.infoAlert(player,
                                    this.translator.getUnspacedField(
                                            user.getLanguage(), "authentication_logged_main"
                                    ).replace("%%main_lobby%%", ChatColor.YELLOW +
                                            this.translator.getUnspacedField(user.getLanguage(), "commons_main_lobby")
                                            + ChatColor.AQUA)
                            );
                        } else {
                            ChatAlertLibrary.infoAlert(player,
                                    this.translator.getUnspacedField(
                                            user.getLanguage(), "authentication_logged_secondary"
                                    ).replace("%%game%%", ChatColor.YELLOW +
                                            user.getLastGame()
                                            + ChatColor.AQUA)
                            );
                        }
                    } catch (Unauthorized unauthorized) {
                        Integer newAttempts = this.instance.loginAttempts.get(player.getUniqueId());
                        if (newAttempts >= 3) {
                            Date remainingDate = TimeUtils.addMinutes(new Date(), 3);
                            this.authenticationAttemptsHandler.setAttemptLock(
                                    player.getUniqueId().toString(),
                                    TimeUtils.getUnixStamp(remainingDate).toString()
                            );
                            Bukkit.getScheduler().runTask(this.instance, () -> player.kickPlayer(ChatColor.RED +
                                    this.translator.getUnspacedField(user.getLanguage(),"authentication_too_many_attempts") + "\n\n" + ChatColor.GRAY +
                                    this.translator.getUnspacedField(user.getLanguage(), "authentication_try_again_delay") +
                                    ": " + this.authenticationAttemptsHandler.getAttemptLockDelay(player.getUniqueId().toString())
                            ));
                            this.instance.loginAttempts.remove(player.getUniqueId());
                        } else {
                            ChatAlertLibrary.errorChatAlert(player,
                                    this.translator.getField(user.getLanguage(), "authentication_incorrect_password") +
                                            ChatColor.GRAY + "[" + newAttempts + "/3]"
                            );
                        }
                    } catch (InternalServerError | NotFound | BadRequest error) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "[Commons Auth] Something went wrong when authenticating player {0} ({1}): {2}",
                                new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
                        player.kickPlayer(ChatColor.RED + this.translator.getUnspacedField(user.getLanguage(), "authentication_login_error") + ". \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName());
                    }
                } else {
                    ChatAlertLibrary.errorChatAlert(player,
                            this.translator.getField(user.getLanguage(),"authentication_not_registered") +
                                    ChatColor.YELLOW + "/register <" +
                                    this.translator.getUnspacedField(user.getLanguage(),"commons_password")
                                    + ">"
                    );
                }
            } else {
                ChatAlertLibrary.errorChatAlert(
                        player,
                        null
                );
            }
        });
        return true;
    }
}