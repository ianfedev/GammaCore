package net.seocraft.commons.bukkit.old.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.CommandContext;
import net.seocraft.commons.bukkit.server.BukkitTokenQuery;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.user.UserRegisterRequest;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.old.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.logging.Level;

public class RegisterCommand extends AbstractAdvancedCommand {

    @Inject private CommonsBukkit instance;
    @Inject private BukkitTokenQuery tokenQuery;
    @Inject private TranslatableField translator;
    @Inject private UserRegisterRequest userRegisterRequest;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private UserStorageProvider userStorageProvider;

    public RegisterCommand() {
        super(
                new String[]{"register", "registro"},
                "/<command> <password>",
                "Command used to register new users at the network",
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
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(this.gameSessionManager.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (this.instance.unregisteredPlayers.contains(player.getUniqueId())) {
                    String password = commandContext.getArgument(0);
                    if (password.length() > 7) {
                        try {
                            this.userRegisterRequest.executeRequest(
                                    player.getName(),
                                    player.getAddress().toString().split(":")[0].replace("/", ""),
                                    password,
                                    this.tokenQuery.getToken()
                            );
                        } catch (InternalServerError | Unauthorized | NotFound | BadRequest error) {
                            Bukkit.getLogger().log(Level.WARNING,
                                    "[Commons Auth] Something went wrong when authenticating player {0} ({1}): {2}",
                                    new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
                            Bukkit.getScheduler().runTask(this.instance, () -> player.kickPlayer(ChatColor.RED +
                                    this.translator.getUnspacedField(user.getLanguage(), "authentication_register_error") +
                                    ". \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName()
                            ));
                        }
                        ChatAlertLibrary.infoAlert(player,
                                ChatColor.AQUA +
                                        this.translator.getUnspacedField(user.getLanguage(), "authentication_welcome_new")
                                                .replace("%%server%%", ChatColor.YELLOW + "Seocraft Network" + ChatColor.AQUA)
                        );
                        //TODO: Give welcome message, handle request, send to server group
                    } else {
                        ChatAlertLibrary.errorChatAlert(player,
                                this.translator.getUnspacedField(user.getLanguage(),"authentication_password_weak")
                        );
                    }
                } else {
                    ChatAlertLibrary.errorChatAlert(player,
                            this.translator.getField(user.getLanguage(),"authentication_already_registered") +
                                    ChatColor.YELLOW + "/login <" +
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