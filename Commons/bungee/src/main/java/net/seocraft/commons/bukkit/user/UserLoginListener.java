package net.seocraft.commons.bukkit.user;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.seocraft.commons.bukkit.CommonsBungee;

public class UserLoginListener implements Listener {

    @EventHandler
    public void userPreLogin(PreLoginEvent event) {
        if (event.isCancelled()) return;
        event.registerIntent(CommonsBungee.getInstance());
        PendingConnection connection = event.getConnection();
        
    }
}
