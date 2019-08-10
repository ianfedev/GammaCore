package net.seocraft.commons.bukkit.listener;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.event.GamePairingEvent;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GamePairingListener implements Listener {

    @Inject private CommonsBukkit instance;

    @EventHandler
    public void gamePairingListener(GamePairingEvent event) {
        Bukkit.getScheduler().cancelTask(this.instance.pairingRunnable);
    }
}
