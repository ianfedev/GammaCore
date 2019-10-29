package net.seocraft.commons.bungee.punishment;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.seocraft.api.core.redis.messager.ChannelListener;
import net.seocraft.api.core.user.UserExpulsion;
import net.seocraft.commons.bungee.CommonsBungee;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.api.core.utils.TimeUtils;

public class PunishmentListener implements ChannelListener<UserExpulsion> {

    private CommonsBungee instance;
    private TranslatableField translator;

    public PunishmentListener(CommonsBungee instance, TranslatableField translator) {
        this.instance = instance;
        this.translator = translator;
    }

    @Override
    public void receiveMessage(UserExpulsion object) {
        if (this.instance.getProxy().getPlayer(object.getUser().getUsername()) != null) {
            ProxiedPlayer player = this.instance.getProxy().getPlayer(object.getUser().getUsername());
            if (!object.isPermanent()) {
                player.disconnect(new TextComponent(
                        ChatColor.RED +
                                this.translator.getField(
                                        object.getUser().getLanguage(), "commons_punish_ban"
                                ) +
                                this.translator.getField(
                                        object.getUser().getLanguage(), "commons_punish_temporal"
                                ).toLowerCase() +
                                ChatColor.GOLD + "\u00BB " + ChatColor.AQUA + object.getReason() + "\n\n" +
                                ChatColor.GRAY + this.translator.getField(
                                object.getUser().getLanguage(),
                                "commons_punish_expires"
                        ) + TimeUtils.formatAgoTimeInt((int) object.getExpiration(), object.getUser().getLanguage()).toLowerCase()
                                + "\n\n" +ChatColor.YELLOW +
                                this.translator.getUnspacedField(
                                        object.getUser().getLanguage(), "commons_punish_appeal"
                                ).replace("%%website%%",
                                        ChatColor.GOLD
                                                + "seocraft.net/apelar"
                                                + ChatColor.YELLOW
                                ))
                );
            } else {
                player.disconnect(new TextComponent(
                        ChatColor.RED +
                                this.translator.getField(
                                        object.getUser().getLanguage(), "commons_punish_ban"
                                ) +
                                this.translator.getField(
                                        object.getUser().getLanguage(), "commons_punish_permanent"
                                ).toLowerCase() +
                                ChatColor.GOLD + "\u00BB " + ChatColor.AQUA + object.getReason() + "\n\n" +
                                ChatColor.YELLOW +
                                this.translator.getUnspacedField(
                                        object.getUser().getLanguage(), "commons_punish_appeal"
                                ).replace("%%website%%",
                                        ChatColor.GOLD
                                                + "seocraft.net/apelar"
                                                + ChatColor.YELLOW
                                )
                        )
                );
            }
        }
    }

}
