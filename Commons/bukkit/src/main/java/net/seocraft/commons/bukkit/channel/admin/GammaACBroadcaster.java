package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.channel.admin.ACBroadcaster;
import net.seocraft.api.bukkit.channel.admin.ACMessage;
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

public class GammaACBroadcaster implements ACBroadcaster {

    @Inject private ACParticipantsProvider participantsProvider;
    @Inject private TranslatableField translatableField;
    @Inject private UserFormatter userFormatter;
    @Inject private BukkitAPI bukkitAPI;

    @Override
    public void deliveryMessage(@NotNull ACMessage message) {
        Set<User> deliverableUsers = this.participantsProvider.getChannelParticipants();

        deliverableUsers.forEach((user) -> {

            Player player = Bukkit.getPlayer(user.getUsername());

            if (player != null) {

                if (user.getGameSettings().getAdminChat().isActive() && !message.isImportant()) {

                    player.sendMessage(
                            ChatColor.AQUA + "[" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_prefix").toUpperCase() + "]" +
                                    " " + this.userFormatter.getUserFormat(message.getSender(), this.bukkitAPI.getConfig().getString("realm")) + ChatColor.WHITE + ": " +
                                    getMessage(message.getMessage(), message.getMentionUsers())
                    );

                    message.getMentionUsers().forEach((mentioned) -> {
                        if (mentioned.getId().equalsIgnoreCase(user.getId())) player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
                    });

                    return;
                }

                if (message.isImportant()) {
                    player.sendMessage(
                            ChatColor.RED + "[" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_ac_prefix").toUpperCase() + "]" +
                                    " " + this.userFormatter.getUserFormat(message.getSender(), this.bukkitAPI.getConfig().getString("realm")) + ChatColor.WHITE + ": " +
                                    getMessage(message.getMessage(), message.getMentionUsers())
                    );
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 2f);
                }

            }

        });

    }

    private @NotNull String getMessage(@NotNull String message, @NotNull Set<User> users) {
        for (User user : users)
            message = message.replace("@" + user.getDisplay(), ChatColor.YELLOW + "@" + user.getDisplay() + ChatColor.WHITE);
        return message;
    }

}
