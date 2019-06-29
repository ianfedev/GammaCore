package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class AuthenticationEnvironmentEventsListener implements Listener {

    @Inject private CommonsBukkit instance;
    @Inject private TranslatableField translator;
    @Inject private SessionHandler sessionHandler;
    @Inject private UserStoreHandler userStoreHandler;

    @EventHandler
    public void authenticationMovementListener(PlayerMoveEvent event) {
        if (this.instance.getConfig().getBoolean("authentication.enabled")) {
            Location from = event.getFrom();
            Location to = event.getTo();

            if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()){
                to.setX(from.getBlockX() + .5);
                to.setZ(from.getBlockZ() + .5);

                event.setTo(to);
            }

        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatEvent(AsyncPlayerChatEvent event) {

        if (this.instance.getConfig().getBoolean("authentication.enabled")) {
            Player player = event.getPlayer();
            try {
                ChatAlertLibrary.errorChatAlert(
                        player,
                        this.translator.getUnspacedField(
                                this.userStoreHandler.getCachedUserSync(this.sessionHandler.getCachedSession(player.getName()).getPlayerId()).getLanguage(),
                                "authentication_not_authenticated"
                        ) + "."
                );
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
                ChatAlertLibrary.errorChatAlert(
                        player,
                        null
                );
            } finally {
                event.setCancelled(true);
            }
        }
    }

}
