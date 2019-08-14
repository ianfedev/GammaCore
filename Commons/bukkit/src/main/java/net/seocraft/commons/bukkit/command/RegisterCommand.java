package net.seocraft.commons.bukkit.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.CommandContext;
import net.seocraft.api.bukkit.cloud.CloudLobbySwitcher;
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
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class RegisterCommand extends AbstractAdvancedCommand {

    @Inject private CommonsBukkit instance;
    @Inject private BukkitTokenQuery tokenQuery;
    @Inject private TranslatableField translator;
    @Inject private UserRegisterRequest userRegisterRequest;
    @Inject private ObjectMapper mapper;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private CloudLobbySwitcher cloudLobbySwitcher;
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
        try {
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(this.gameSessionManager.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();
                    if (this.instance.unregisteredPlayers.contains(player.getUniqueId())) {
                        String password = commandContext.getArgument(0);
                        if (password.length() > 7) {
                            try {
                                ObjectNode node = mapper.createObjectNode();
                                node.put("username", player.getName());
                                node.put("ip", player.getAddress().toString().split(":")[0].replace("/", ""));
                                node.put("password", password);

                                this.userRegisterRequest.executeRequest(
                                        this.mapper.writeValueAsString(node),
                                        this.tokenQuery.getToken()
                                );

                                this.cloudLobbySwitcher.sendPlayerToGroup(player, "main_lobby");
                                ChatAlertLibrary.infoAlert(player,
                                        ChatColor.AQUA +
                                                this.translator.getUnspacedField(user.getLanguage(), "authentication_welcome_new")
                                                        .replace("%%server%%", ChatColor.YELLOW + "Seocraft Network" + ChatColor.AQUA)
                                );

                            } catch (InternalServerError | Unauthorized | NotFound | BadRequest | JsonProcessingException error) {
                                Bukkit.getLogger().log(Level.WARNING,
                                        "[Commons Auth] Something went wrong when authenticating player {0} ({1}): {2}",
                                        new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
                                Bukkit.getScheduler().runTask(this.instance, () -> player.kickPlayer(ChatColor.RED +
                                        this.translator.getUnspacedField(user.getLanguage(), "authentication_register_error") +
                                        ". \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName()
                                ));
                            }
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
                            player
                    );
                }
            });
        } catch (IOException e) {
            ChatAlertLibrary.errorChatAlert(
                    player
            );
        }
        return true;
    }
}