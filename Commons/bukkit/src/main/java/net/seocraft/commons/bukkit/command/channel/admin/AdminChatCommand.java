package net.seocraft.commons.bukkit.command.channel.admin;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.basic.ArgumentArray;
import me.fixeddev.bcm.basic.Namespace;
import net.seocraft.api.bukkit.channel.admin.ACMessageManager;
import net.seocraft.api.bukkit.channel.admin.ACParticipantsProvider;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class AdminChatCommand extends AbstractAdvancedCommand {

    @Inject private ACMessageManager messageManager;
    @Inject private ACParticipantsProvider participantsProvider;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;

    public AdminChatCommand() {
        super(new String[]{"adminchat", "ac"});
        setDescription("Command user to communicate in the admin channel");
        setPermission("commons.staff.chat");
        setExpectedFlags(Lists.newArrayList('i'));
        setUsage("/<command> <message...> [-i]");
        setMinArguments(1);
    }

    @Override
    public boolean execute(CommandContext commandContext) {
        Player sender = (Player) commandContext.getNamespace().getObject(CommandSender.class, "sender");
        String message = commandContext.getJoinedArgs(0).replace("-", "");
        boolean important = commandContext.getFlagValue('i');

        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(sender.getDatabaseIdentifier()), userResponse -> {
            if (userResponse.getStatus().equals(AsyncResponse.Status.SUCCESS)) {
                User user = userResponse.getResponse();
                try {

                    if (important && !sender.hasPermission("commons.staff.chat.important")) {
                        ChatAlertLibrary.errorChatAlert(
                                sender,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_ac_permission"
                                )
                        );
                        return;
                    }

                    this.messageManager.sendMessage(message, userResponse.getResponse(), important);
                } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "[Commons] There was an error with an admin message.", e);
                    ChatAlertLibrary.errorChatAlert(
                            sender,
                            this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_ac_error"
                            )
                    );
                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, "[Commons] There was an error with an admin message.", userResponse.getThrowedException());
                ChatAlertLibrary.errorChatAlert(sender);
            }
        });

        return true;
    }

    @Override
    public List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) {
        if (arguments.getSize() > 0) {
            String getLastArgument = arguments.get(arguments.getPosition() + 1);
            if(getLastArgument.startsWith("@"))
                return this.participantsProvider.getChannelParticipants().stream().map(u -> "@" + u.getUsername()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


}
