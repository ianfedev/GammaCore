package net.seocraft.commons.bukkit.punishment;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.minecraft.PlayerTitleHandler;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.serialization.TimeUtils;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PunishmentActions {

    @Inject private TranslatableField translator;

    public void banPlayer(Player target, User targetData, Punishment punishment) {
        if (!punishment.isPermanent()) {
            target.kickPlayer(ChatColor.RED +
                            this.translator.getField(
                                    targetData.getLanguage(), "commons_punish_ban"
                            ) +
                            this.translator.getField(
                                    targetData.getLanguage(), "commons_punish_temporal"
                            ).toLowerCase() +
                            ChatColor.GOLD + "\u00BB " + ChatColor.AQUA + punishment.getReason() + "\n\n" +
                            ChatColor.GRAY + this.translator.getField(
                    targetData.getLanguage(),
                    "commons_punish_expires"
                    ) + TimeUtils.formatAgoTimeInt((int) punishment.getExpiration(), targetData.getLanguage()).toLowerCase()
                            + "\n\n" +ChatColor.YELLOW +
                            this.translator.getUnspacedField(
                                    targetData.getLanguage(), "commons_punish_appeal"
                            ).replace("%%website%%",
                                    ChatColor.GOLD
                                            + "seocraft.net/apelar"
                                            + ChatColor.YELLOW
                            )
            );
        } else {
            target.kickPlayer(ChatColor.RED +
                    this.translator.getField(
                            targetData.getLanguage(), "commons_punish_ban"
                    ) +
                    this.translator.getField(
                            targetData.getLanguage(), "commons_punish_permanent"
                    ).toLowerCase() +
                    ChatColor.GOLD + "\u00BB " + ChatColor.AQUA + punishment.getReason() + "\n\n" +
                    ChatColor.YELLOW +
                    this.translator.getUnspacedField(
                            targetData.getLanguage(), "commons_punish_appeal"
                    ).replace("%%website%%",
                            ChatColor.GOLD
                                    + "seocraft.net/apelar"
                                    + ChatColor.YELLOW
                    )
            );
        }
    }


    public void kickPlayer(Player target, User targetData, Punishment punishment) {
        target.kickPlayer(ChatColor.RED +
                this.translator.getField(
                        targetData.getLanguage(), "commons_punish_kick"
                ) +
                ChatColor.GOLD + "\u00BB " + ChatColor.AQUA + punishment.getReason() + "\n\n" +
                ChatColor.YELLOW +
                this.translator.getUnspacedField(
                        targetData.getLanguage(), "commons_punish_appeal"
                ).replace("%%website%%",
                        ChatColor.GOLD
                                + "seocraft.net/apelar"
                                + ChatColor.YELLOW
                )
        );
    }

    public void warnPlayer(Player target, User targetData, Punishment punishment) {
        PlayerTitleHandler.sendTitle(
                target,
                ChatColor.YELLOW + "\u26A0 " + ChatColor.RED +
                        this.translator.getUnspacedField(
                                targetData.getLanguage(),
                                "commons_punish_warn"
                        ).toUpperCase() +
                ChatColor.YELLOW + " \u26A0",
                ChatColor.AQUA + punishment.getReason()
        );
        target.playSound(target.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
    }
}
