package net.seocraft.commons.bukkit.friend;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserChat;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.util.ChatGlyphs;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FriendshipUserActions {

    @Inject private TranslatableField translatableField;
    @Inject private UserChat userChatHandler;
    @Inject private BukkitAPI bukkitAPI;

    public void senderAction(Player player, User user, User targetRecord, FriendshipAction action) {
        switch (action) {
            case CREATE: {
                player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                player.sendMessage(
                        ChatColor.YELLOW +
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_friends_request_sent"
                                ).replace(
                                        "%%player%%",
                                        this.userChatHandler.getUserFormat(
                                                targetRecord,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        ) + ChatColor.YELLOW
                                ) + "."
                );
                player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
                break;
            }
            default: break;
        }
    }

    public void receiverAction(User sender, User target, FriendshipAction action) {
        Player player = Bukkit.getPlayer(target.getUsername());
        String l = target.getLanguage();
        String realm = this.bukkitAPI.getConfig().getString("realm");
        String senderPlaceholder = this.userChatHandler.getUserFormat(sender, realm);
        String targetPlaceholder = this.userChatHandler.getUserFormat(target, realm);
        if (player != null) {
            player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

            switch (action) {
                case CREATE: {
                    player.sendMessage(ChatColor.YELLOW +
                            this.translatableField.getUnspacedField(
                                    l,
                                    "commons_friends_received_request"
                            ).replace("%%player%%", targetPlaceholder + ChatColor.YELLOW)
                    );

                    // [ACCEPT] Button
                    TextComponent acceptComponent = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "[" +
                            this.translatableField.getUnspacedField(l, "commons_accept").toUpperCase() + "]");
                    acceptComponent.setHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder( ChatColor.YELLOW +
                                    this.translatableField.getUnspacedField(l, "commons_friends_accept_placeholder")
                                            .replace("%%player%%", senderPlaceholder + ChatColor.YELLOW)
                            ).create()
                    ));
                    acceptComponent.setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/friends accept " + sender.getUsername()
                    ));

                    // [REJECT] Button
                    TextComponent rejectComponent = new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "[" +
                            this.translatableField.getUnspacedField(l, "commons_reject").toUpperCase() + "]");
                    rejectComponent.setHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder( ChatColor.YELLOW +
                                    this.translatableField.getUnspacedField(l, "commons_friends_reject_placeholder")
                                            .replace("%%player%%", senderPlaceholder + ChatColor.YELLOW)
                            ).create()
                    ));
                    rejectComponent.setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/friends reject " + sender.getUsername()
                    ));

                    TextComponent finalComponent = new TextComponent(acceptComponent);
                    finalComponent.addExtra(" ");
                    finalComponent.addExtra(rejectComponent);

                    player.sendMessage(finalComponent);
                    break;
                }
                default: break;
            }

            player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
        }

    }
}
