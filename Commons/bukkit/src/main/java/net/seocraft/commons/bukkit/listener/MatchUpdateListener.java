package net.seocraft.commons.bukkit.listener;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.event.MatchUpdateEvent;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.logging.Level;

public class MatchUpdateListener implements Listener {

    @Inject private CommonsBukkit instance;
    @Inject private ServerManager serverManager;

    @EventHandler
    public void gamePairingListener(MatchUpdateEvent event) {
        this.instance.getServerRecord().addMatch(event.getMatch());
        try {
            this.serverManager.updateServer(
                this.instance.getServerRecord()
            );
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException error) {
            Bukkit.getLogger().log(Level.SEVERE, "[Game API] There was an error updating match. ({0})", error.getMessage());
        }
    }

}
