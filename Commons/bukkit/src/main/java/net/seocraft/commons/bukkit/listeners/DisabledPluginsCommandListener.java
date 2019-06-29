package net.seocraft.commons.bukkit.listeners;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class DisabledPluginsCommandListener implements Listener {

    @Inject
    private TranslatableField translator;
    @Inject
    private SessionHandler sessionHandler;
    @Inject
    private UserStoreHandler userStoreHandler;


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        try {
            String userLanguage = this.userStoreHandler.getCachedUserSync(this.sessionHandler.getCachedSession(player.getName()).getPlayerId()).getLanguage();

            if (event.getMessage().equals("/pl") || event.getMessage().equals("/plugins")) {
                ChatAlertLibrary.infoAlert(
                        player,
                        this.translator.getField(userLanguage, "commons_plugin_developer") + ChatColor.YELLOW + "www.seocraft.net/staff"
                );

                event.setCancelled(true);
            }
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    null
            );
        }

    }
}
