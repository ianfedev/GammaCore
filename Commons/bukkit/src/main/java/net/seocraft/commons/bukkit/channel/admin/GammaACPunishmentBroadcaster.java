package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.channel.admin.ACParticipantsProvider;
import net.seocraft.api.bukkit.channel.admin.ACPunishmentBroadcaster;
import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.bukkit.punishment.PunishmentType;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.utils.TimeUtils;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GammaACPunishmentBroadcaster implements ACPunishmentBroadcaster {

    @Inject private ACParticipantsProvider participantsProvider;
    @Inject private TranslatableField translatableField;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private UserFormatter userFormatter;

    @Override
    public void broadcastPunishment(@NotNull Punishment punishment) {
        String r = this.bukkitAPI.getConfig().getString("realm");

        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(punishment.getPunished()), punishedRecord -> {
            if (punishedRecord.getStatus().equals(AsyncResponse.Status.SUCCESS)) {
                User punished = punishedRecord.getResponse();
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(punishment.getIssuer()), punisherRecord -> {
                    if (punisherRecord.getStatus().equals(AsyncResponse.Status.SUCCESS)) {
                        User punisher = punisherRecord.getResponse();
                        this.participantsProvider.getChannelParticipants().forEach((user) -> {
                            Player player = Bukkit.getPlayer(user.getUsername());

                            if (player != null && user.getGameSettings().getAdminChat().hasActivePunishments()) {

                                String message = "";
                                message = message + ChatColor.AQUA + "[" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_prefix").toUpperCase() + "] " + ChatColor.RESET;
                                message = message + this.userFormatter.getUserFormat(
                                        punisher,
                                        r
                                ) + " " + ChatColor.GRAY + this.translatableField.getField(user.getLanguage(), "commons_ac_punishment_has").toLowerCase();

                                // Checks punishment type

                                if (punishment.getType().equals(PunishmentType.WARN))
                                    message = message + ChatColor.GREEN + this.translatableField.getField(user.getLanguage(), "commons_ac_punishment_warned").toLowerCase();
                                if (punishment.getType().equals(PunishmentType.KICK))
                                    message = message + ChatColor.YELLOW + this.translatableField.getField(user.getLanguage(), "commons_ac_punishment_kicked").toLowerCase();
                                if (punishment.getType().equals(PunishmentType.BAN))
                                    message = message + ChatColor.RED + this.translatableField.getField(user.getLanguage(), "commons_ac_punishment_banned").toLowerCase();
                                message = message + ChatColor.GRAY + this.translatableField.getField(user.getLanguage(), "commons_ac_punishment_to").toLowerCase();

                                message = message + this.userFormatter.getUserFormat(
                                        punished,
                                        r
                                ) + " " + ChatColor.GRAY;

                                // Checks reason and expiration time
                                if (!punishment.getReason().equalsIgnoreCase(""))
                                    message = message + this.translatableField.getField(user.getLanguage(), "commons_ac_punishment_due").toLowerCase() + punishment.getReason();
                                if (punishment.getExpiration() != -1)
                                    message = message + this.translatableField.getField(user.getLanguage(), "commons_ac_punishment_during").toLowerCase() + TimeUtils.formatAgoTimeInt((int) punishment.getExpiration(), user.getLanguage()).toLowerCase();

                                player.sendMessage(message + ".");
                                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 2f);
                            }

                        });
                    }
                });
            }
        });
    }

}
