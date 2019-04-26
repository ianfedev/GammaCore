package net.seocraft.commons.bukkit.commands;

import com.google.inject.Inject;
import me.ggamer55.bcm.AbstractAdvancedCommand;
import me.ggamer55.bcm.CommandContext;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.bukkit.user.UserStore;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.user.UserRegisterRequest;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.logging.Level;

public class RegisterCommand extends AbstractAdvancedCommand {

    @Inject private CommonsBukkit instance;
    @Inject private ServerTokenQuery tokenQuery;
    @Inject private TranslatableField translator;
    @Inject private UserRegisterRequest userRegisterRequest;
    @Inject private UserStore userStorage;

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
        CallbackWrapper.addCallback(this.userStorage.getUserObject(player.getName()), user -> {
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
        });
        return true;
    }
}