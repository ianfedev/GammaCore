package net.seocraft.commons.bukkit.listeners;

import com.google.inject.Inject;
import net.seocraft.api.shared.session.SessionHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserLogoutListener implements Listener {

    @Inject
    private SessionHandler sessionHandler;

    @EventHandler
    public void onUserLogout(PlayerQuitEvent event){
        sessionHandler.removeGameSession(event.getPlayer().getName()); // TODO: Do it at the commons bungee
    }
}
