package net.seocraft.commons.bukkit.user;

import net.seocraft.api.shared.user.model.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LobbyConnectionEvent extends Event {

    private HandlerList handlerList;
    private User playerRecord;
    private Player player;

    public LobbyConnectionEvent(User playerRecord, Player player) {
        this.playerRecord = playerRecord;
        this.player = player;
        this.handlerList = new HandlerList();
    }

    @Override
    public HandlerList getHandlers() {
        return this.handlerList;
    }

    public User getPlayerRecord() {
        return this.playerRecord;
    }

    public Player getPlayer() {
        return this.player;
    }
}
