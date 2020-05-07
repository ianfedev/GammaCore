package net.seocraft.commons.bukkit.command.channel.admin;

import com.google.inject.Inject;
import me.fixeddev.ebcm.Command;
import me.fixeddev.ebcm.CommandAction;
import me.fixeddev.ebcm.CommandContext;
import me.fixeddev.ebcm.CommandData;
import me.fixeddev.ebcm.ImmutableCommand;
import me.fixeddev.ebcm.SuggestionProvider;
import me.fixeddev.ebcm.bukkit.parameter.provider.PlayerSenderProvider;
import me.fixeddev.ebcm.exception.CommandException;
import me.fixeddev.ebcm.part.ArgumentPart;
import me.fixeddev.ebcm.part.CommandPart;
import me.fixeddev.ebcm.part.FlagPart;
import me.fixeddev.ebcm.part.InjectedValuePart;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.channel.admin.ACMessageManager;
import net.seocraft.api.bukkit.channel.admin.ACParticipantsProvider;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class NewAdminChatCommand implements CommandAction {

    public Command createComand() {
        return ImmutableCommand.builder(CommandData.builder("ac")
                .setDescription("Command user to communicate in the admin channel"))
                .setPermission("commons.staff.chat")
                .addPart(InjectedValuePart.builder("sender", Player.class)
                        .addModifier(PlayerSenderProvider.SENDER_MODIFIER)
                        .setRequired(true)
                        .build())
                .addPart(ArgumentPart.builder("message", String.class)
                        .setRequired(true)
                        .setConsumedArguments(-1)
                        .setSuggestionProvider(new PlayerSuggestor())
                        .build())
                .addPart(FlagPart.builder("important", 'i')
                        .build())
                .setAction(this)
                .build();
    }


    @Inject
    private ACMessageManager messageManager;
    @Inject
    private ACParticipantsProvider participantsProvider;
    @Inject
    private UserStorageProvider userStorageProvider;
    @Inject
    private TranslatableField translatableField;


    @Override
    public boolean execute(CommandContext commandContext) throws CommandException {
        CommandPart senderPart = commandContext.getParts("sender").get(0);
        CommandPart messagePart = commandContext.getParts("message").get(0);
        CommandPart flagPart = commandContext.getParts("important").get(0);

        // Is required, so, the value should be present
        Player sender = (Player) commandContext.getValue(senderPart).get();
        String message = ((String) commandContext.getValue(messagePart).get()).replace("-", "");
        boolean important = (boolean) commandContext.getValue(flagPart).get();

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

    class PlayerSuggestor implements SuggestionProvider {

        @Override
        public List<String> getSuggestions(String s) {
            String[] args = s.split(" ");
            if (args.length > 0) {
                String getLastArgument = args[args.length - 1];
                if (getLastArgument.startsWith("@"))
                    return participantsProvider.getChannelParticipants().stream().map(u -> "@" + u.getUsername()).collect(Collectors.toList());
            }

            return new ArrayList<>();
        }
    }
}
