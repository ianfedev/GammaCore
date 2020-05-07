package net.seocraft.commons.bukkit.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.session.PremiumStatusManager;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.ChatGlyphs;
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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class PremiumCommand implements CommandClass {

    @Inject
    private UserStorageProvider userStorageProvider;
    @Inject
    private PremiumStatusManager premiumStatusManager;
    @Inject
    private TranslatableField translatableField;

    @ACommand(names = {"premium"})
    public boolean holderCommand(@Injected(true) @Sender Player player) {

        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (this.premiumStatusManager.canEnablePremium(user))
                    ChatAlertLibrary.errorChatAlert(
                            player,
                            this.translatableField.getUnspacedField(user.getLanguage(), "commons_premium_validation_error")
                    );

                if (!user.getSessionInfo().isPremium()) {
                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

                    // Base component
                    TextComponent baseComponent = new TextComponent(this.translatableField.getField(user.getLanguage(), "commons_premium_validate_warning"));
                    baseComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);

                    // Accept button
                    TextComponent acceptButton = new TextComponent("[" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_premium_validate_holder") + "]");
                    acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    acceptButton.setBold(true);
                    acceptButton.setHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(
                                    ChatColor.GREEN + this.translatableField.getUnspacedField(user.getLanguage(), "commons_premium_validate_hover")
                            ).create()
                    ));
                    acceptButton.setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/premiumswitch"
                    ));
                    player.spigot().sendMessage(baseComponent);
                    player.spigot().sendMessage(acceptButton);

                    player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                } else {
                    player.performCommand("premiumswitch");
                }
            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }
        });
        return true;
    }

    @ACommand(names = {"premiumswitch"})
    public boolean mainCommand(@Injected(true) @Sender Player player) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                if (this.premiumStatusManager.canEnablePremium(user))
                    ChatAlertLibrary.errorChatAlert(
                            player,
                            this.translatableField.getUnspacedField(user.getLanguage(), "commons_premium_validation_error")
                    );

                try {
                    boolean premium = this.premiumStatusManager.togglePremiumStatus(user);
                    if (premium) {
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
                } catch (Unauthorized | JsonProcessingException | BadRequest | NotFound | InternalServerError ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "[Commons] There was an error updating premium status", ex);
                    ChatAlertLibrary.errorChatAlert(player);
                }
            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }
        });
        return true;
    }

}
