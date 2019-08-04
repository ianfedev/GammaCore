package net.seocraft.commons.bukkit.listener;

import com.google.inject.Inject;
import net.seocraft.api.core.session.GameSessionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserLogoutListener implements Listener {

    @Inject
    private GameSessionManager gameSessionManager;

    @EventHandler
    public void onUserLogout(PlayerQuitEvent event){
        gameSessionManager.removeGameSession(event.getPlayer().getName()); // TODO: Do it at the commons bungee
    }
}
