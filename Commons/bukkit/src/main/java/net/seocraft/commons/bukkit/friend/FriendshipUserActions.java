package net.seocraft.commons.bukkit.friend;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.friend.FriendshipAction;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.util.ChatGlyphs;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class FriendshipUserActions {

    @Inject private TranslatableField translatableField;
    @Inject private UserFormatter userFormatter;
    @Inject private BukkitAPI bukkitAPI;

    public void senderAction(Player player, User user, User targetRecord, FriendshipAction action, User issuer) {
        String realm = this.bukkitAPI.getConfig().getString("realm");

        player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
        switch (action) {
            case CREATE: {
                player.sendMessage(
                        ChatColor.YELLOW +
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_friends_request_sent"
                                ).replace(
                                        "%%player%%",
                                        this.userFormatter.getUserFormat(
                                                targetRecord,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        ) + ChatColor.YELLOW
                                ) + "."
                );
                break;
            }
            case ACCEPT: {
                showNewFriendshipMessage(player, user, targetRecord);
            }
            case FORCE: {
                String replacableString = this.translatableField.getUnspacedField(issuer.getLanguage(), "commons_friends_forced_success");
                if (user.getId().equalsIgnoreCase(issuer.getId())) {
                    replacableString
                            .replace(
                                    "%%firstUser%%",
                                    this.userFormatter.getUserFormat(targetRecord, realm)
                            )
                            .replace(
                                    "%%senderUser%%",
                                    this.translatableField.getUnspacedField(issuer.getLanguage(), "commons_you")
                                            .toLowerCase()
                            );
                } else {
                    replacableString
                            .replace(
                                    "%%firstUser%%",
                                    this.userFormatter.getUserFormat(user, realm)
                            )
                            .replace(
                                    "%%secondUser%%",
                                    this.userFormatter.getUserFormat(targetRecord, realm)
                            );
                }

                player.sendMessage(
                        ChatColor.LIGHT_PURPLE +
                                replacableString
                );
            }
            default: break;
        }
        player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
    }

    public void receiverAction(User sender, User target, FriendshipAction action, @Nullable User issuer) {
        Player player = Bukkit.getPlayer(target.getUsername());
        String l = target.getLanguage();
        String realm = this.bukkitAPI.getConfig().getString("realm");
        String senderPlaceholder = this.userFormatter.getUserFormat(sender, realm);

        // Get FORCED action to "first user"
        Player firstUser = Bukkit.getPlayer(sender.getUsername());
        if (action == FriendshipAction.FORCE && firstUser != null && issuer != null) {
            String replaceString = this.translatableField.getUnspacedField(sender.getLanguage(), "commons_friends_forced_friendship")
                    .replace("%%sender%%", this.userFormatter.getUserFormat(issuer, realm))
                    .replace("%%target%%", this.userFormatter.getUserFormat(target, realm));
            firstUser.sendMessage(ChatColor.LIGHT_PURPLE + replaceString);
        }

        if (player != null) {
            player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());

            switch (action) {
                case CREATE: {
                    player.sendMessage(ChatColor.YELLOW +
                            this.translatableField.getUnspacedField(
                                    l,
                                    "commons_friends_received_request"
                            ).replace("%%player%%", senderPlaceholder + ChatColor.YELLOW)
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
                case ACCEPT: {
                    showNewFriendshipMessage(player, target, sender);
                }
                case FORCE: {
                    if (issuer != null) {
                        if (sender.getId().equalsIgnoreCase(issuer.getId())) {
                            String replaceString = this.translatableField.getUnspacedField(l, "commons_friends_forced_friendship")
                            .replace("%%sender%%", this.userFormatter.getUserFormat(issuer, realm))
                            .replace("%%target%%", this.translatableField.getUnspacedField(l, "commons_gender_him_her").toLowerCase());
                            player.sendMessage(ChatColor.LIGHT_PURPLE + replaceString);

                        } else {
                            String replaceTargetString = this.translatableField.getUnspacedField(l, "commons_friends_forced_friendship")
                                    .replace("%%sender%%", this.userFormatter.getUserFormat(issuer, realm))
                                    .replace("%%target%%", this.userFormatter.getUserFormat(sender, realm));
                            player.sendMessage(ChatColor.LIGHT_PURPLE + replaceTargetString);
                        }
                    }
                }
                default: break;
            }

            player.sendMessage(ChatColor.AQUA + ChatGlyphs.SEPARATOR.getContent());
        }

    }

    private void showNewFriendshipMessage(Player player, User user, User friendshipShowable) {
        player.sendMessage(
                ChatColor.YELLOW +
                        this.translatableField.getUnspacedField(
                                user.getLanguage(),
                                "commons_friends_request_accepted"
                        ).replace(
                                "%%player%%",
                                this.userFormatter.getUserFormat(
                                        friendshipShowable,
                                        this.bukkitAPI.getConfig().getString("realm")
                                ) + ChatColor.YELLOW
                        )
        );
    }
}
