package net.seocraft.commons.bukkit.user;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.bukkit.user.UserLobbyMessageHandler;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class GammaUserLobbyMessageHandler implements UserLobbyMessageHandler {

    @Inject private TranslatableField translatableField;
    @Inject private UserFormatter userFormatter;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private BukkitAPI bukkitAPI;

    @Override
    public void alertUserJoinMessage(@NotNull User user) {
        String mainGroup = user.getPrimaryGroup().getId();
        Bukkit.getOnlinePlayers().forEach((player) -> CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User playerRecord = userAsyncResponse.getResponse();
                String personalField = this.translatableField.getUnspacedField(playerRecord.getLanguage(), "joinalert_" + user.getId());
                if (!personalField.equalsIgnoreCase("joinalert_" + user.getId())) {
                    player.sendMessage(
                            ChatAlertLibrary.transformChat(
                                    personalField.replace("%%player%%", this.userFormatter.getUserFormat(user, this.bukkitAPI.getConfig().getString("realm")))
                            )
                    );
                    return;
                }

                String translatedField = this.translatableField.getUnspacedField(playerRecord.getLanguage(), "joinalert_" + mainGroup);
                if (!translatedField.equalsIgnoreCase("joinalert_" + mainGroup)) {
                    player.sendMessage(
                            ChatAlertLibrary.transformChat(
                                    translatedField.replace("%%player%%", this.userFormatter.getUserFormat(user, this.bukkitAPI.getConfig().getString("realm")))
                            )
                    );
                }

            } else {
                Bukkit.getLogger().log(Level.WARNING, "[Commons] Error sending join message to player {0}.", player.getName());
            }
        }));
    }
}
