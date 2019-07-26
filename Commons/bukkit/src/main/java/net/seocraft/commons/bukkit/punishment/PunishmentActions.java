package net.seocraft.commons.bukkit.punishment;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.bukkit.punishment.PunishmentProvider;
import net.seocraft.api.bukkit.punishment.PunishmentType;
import net.seocraft.commons.bukkit.minecraft.PlayerTitleHandler;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.utils.TimeUtils;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Date;

public class PunishmentActions {

    @Inject private TranslatableField translator;
    @Inject private CommonsBukkit instance;
    @Inject private PunishmentProvider punishmentProvider;

    public void banPlayer(Player target, User targetData, Punishment punishment) {
        if (target == null) return;
        Bukkit.getScheduler().runTask(this.instance, () -> {
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
        });
    }


    public void kickPlayer(Player target, User targetData, Punishment punishment) {
        if (target == null) return;
        Bukkit.getScheduler().runTask(this.instance, () -> target.kickPlayer(ChatColor.RED +
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
        ));

    }

    public void warnPlayer(Player target, User targetData, Punishment punishment) {
        if (target == null) return;
        Bukkit.getScheduler().runTask(this.instance, () -> {
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
        });

    }

    public void checkBan(Player target, User targetData) throws InternalServerError {
        try {
            @Nullable Punishment lastPunishment = this.punishmentProvider.getLastPunishmentSync(PunishmentType.BAN, targetData.getId());

            if (lastPunishment == null || lastPunishment.getId() == null || !lastPunishment.isActive()) {
                return;
            }

            if (!lastPunishment.isPermanent()) {
                Date expireDate = TimeUtils.parseUnixStamp((int) lastPunishment.getExpiration());
                if (expireDate.before(new Date())) {
                    lastPunishment.setActive(false);
                    this.punishmentProvider.updatePunishmentSync(lastPunishment);
                    return;
                }
            }

            banPlayer(target, targetData, lastPunishment);
        } catch (Unauthorized | BadRequest | InternalServerError | NotFound | IOException error) {
            throw new InternalServerError(error.getMessage());
        }
    }
}
