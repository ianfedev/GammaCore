package net.seocraft.commons.bukkit.command.channel.admin;

import com.google.inject.Inject;
import me.fixeddev.ebcm.*;
import me.fixeddev.ebcm.part.ArgumentPart;
import me.fixeddev.ebcm.part.FlagPart;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.channel.admin.ACMessageManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

public class AdminChatCommand implements CommandAction {

    @Inject private ACMessageManager messageManager;
    //@Inject private ACParticipantsProvider participantsProvider;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;

    public @NotNull Command getCommand() {
        return ImmutableCommand.builder(CommandData.builder("ac"))
                .addPart(ArgumentPart.builder("message", String.class).setConsumedArguments(-1).build())
                .addPart(FlagPart.builder("important", 'i').build())
                .setPermission("commons.staff.chat")
                .setAction(this)
                .build();
    }

    @Override
    public boolean execute(CommandContext commandContext) {
        Player sender = (Player) commandContext.getObject(CommandSender.class, "SENDER");
        Optional<String> message = commandContext.getValue(commandContext.getParts("password").get(0));
        Optional<Boolean> important = commandContext.getValue(commandContext.getParts("important").get(0));

        message.ifPresent(s -> CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(sender.getDatabaseIdentifier()), userResponse -> {
            if (userResponse.getStatus().equals(AsyncResponse.Status.SUCCESS)) {
                User user = userResponse.getResponse();
                try {

                    if (important.isPresent() && important.get() && !sender.hasPermission("commons.staff.chat.important")) {
                        ChatAlertLibrary.errorChatAlert(
                                sender,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_ac_permission"
                                )
                        );
                        return;
                    }

                    if (!user.getGameSettings().getAdminChat().isActive()) {
                        TextComponent disabled = new TextComponent(this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_disabled") + ". ");
                        disabled.setColor(ChatColor.RED);
                        TextComponent hover = new TextComponent(this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_reminder_click"));
                        hover.setColor(ChatColor.YELLOW);
                        hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acs"));
                        hover.setHoverEvent(
                                new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder(
                                                ChatColor.YELLOW +
                                                        this.translatableField.getUnspacedField(
                                                                user.getLanguage(),
                                                                "commons_ac_reminder_click"
                                                        )
                                        ).create()
                                )
                        );
                        disabled.addExtra(hover);
                        sender.sendMessage(disabled);
                        return;
                    }

                    this.messageManager.sendMessage(s, userResponse.getResponse(), (important.isPresent() && important.get()));
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
        }));

        return true;
    }

    // TODO: Implement when implemented at EBCM

    /*public List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) {
        if (arguments.getSize() > 0) {
            String getLastArgument = arguments.get(arguments.getPosition() + 1);
            if(getLastArgument.startsWith("@"))
                return this.participantsProvider.getChannelParticipants().stream().map(u -> "@" + u.getUsername()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }*/


}
