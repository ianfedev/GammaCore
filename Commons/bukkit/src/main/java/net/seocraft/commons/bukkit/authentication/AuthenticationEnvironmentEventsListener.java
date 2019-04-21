package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.user.UserStore;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class AuthenticationEnvironmentEventsListener implements Listener {

    private CommonsBukkit instance = CommonsBukkit.getInstance();
    @Inject private TranslatableField translator;
    @Inject private UserStore userStorage;

    @EventHandler
    public void authenticationMovementListener(PlayerMoveEvent event) {
        if (this.instance.getConfig().getBoolean("authentication.enabled")) event.setTo(event.getFrom());
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatAlertLibrary.errorChatAlert(
                player,
                this.translator.getUnspacedField(
                        this.userStorage.getUserObjectSync(player.getName()).getLanguage(),
                        "authentication_not_authenticated"
                ) + "."
        );
        event.setCancelled(true);
    }

}
