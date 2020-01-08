package net.seocraft.commons.bukkit.listener;

import com.google.inject.Inject;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.IOException;

public class DisabledPluginsCommandListener implements Listener {

    @Inject
    private TranslatableField translator;
    @Inject
    private UserStorageProvider userStorageProvider;


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        try {
            String userLanguage = this.userStorageProvider.getCachedUserSync(player.getDatabaseIdentifier()).getLanguage();

            if (event.getMessage().equals("/pl") || event.getMessage().equals("/plugins")) {
                ChatAlertLibrary.infoAlert(
                        player,
                        this.translator.getField(userLanguage, "commons_plugin_developer") + ChatColor.YELLOW + "www.seocraft.net/staff"
                );

                event.setCancelled(true);
            }
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException exception) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    null
            );
        }
    }
}
