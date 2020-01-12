package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.channel.admin.ACLoginBroadcaster;
import net.seocraft.api.bukkit.channel.admin.ACParticipantsProvider;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GammaACLoginBroadcaster implements ACLoginBroadcaster {

    @Inject private ACParticipantsProvider participantsProvider;
    @Inject private TranslatableField translatableField;
    @Inject private UserFormatter userFormatter;
    @Inject private BukkitAPI bukkitAPI;


    @Override
    public void broadcastLogin(@NotNull User session, boolean important) {
        Set<User> deliverableUsers = this.participantsProvider.getChannelParticipants();
        String r = this.bukkitAPI.getConfig().getString("realm");

        deliverableUsers.forEach((user) -> {

            Player player = Bukkit.getPlayer(user.getUsername());

            if (player != null) {
                if (user.hasAdminChatActive() && !important) {

                    player.sendMessage(
                            ChatColor.AQUA + "[" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_prefix").toUpperCase() + "]" +
                                    " " + this.userFormatter.getUserFormat(session, r) + " " + ChatColor.YELLOW +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_login") + "."
                    );
                    return;
                }

                if (important) {
                    player.sendMessage(
                            ChatColor.RED + "[" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_prefix").toUpperCase() + "]" +
                                    " " + this.userFormatter.getUserFormat(session, r) + " " + ChatColor.YELLOW +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_login") + "."
                    );
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 2f);
                }
            }
        });
    }

    @Override
    public void broadcastLogout(@NotNull User session, boolean important) {
        Set<User> deliverableUsers = this.participantsProvider.getChannelParticipants();
        String r = this.bukkitAPI.getConfig().getString("realm");

        deliverableUsers.forEach((user) -> {

            Player player = Bukkit.getPlayer(user.getUsername());

            if (player != null) {
                if (user.hasAdminChatActive() && !important) {

                    player.sendMessage(
                            ChatColor.AQUA + "[" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_prefix").toUpperCase() + "]" +
                                    " " + this.userFormatter.getUserFormat(session, r) + " " + ChatColor.YELLOW +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_logout") + "."
                    );
                    return;
                }

                if (important) {
                    player.sendMessage(
                            ChatColor.RED + "[" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_prefix").toUpperCase() + "]" +
                                    " " + this.userFormatter.getUserFormat(session, r) + " " + ChatColor.YELLOW +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_logout") + "."
                    );
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 2f);
                }

            }
        });
    }

}
