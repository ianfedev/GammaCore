package net.seocraft.commons.bukkit.user;

import net.seocraft.api.shared.user.model.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LobbyConnectionEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    private User playerRecord;
    private Player player;

    public LobbyConnectionEvent(User playerRecord, Player player) {
        this.playerRecord = playerRecord;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public User getPlayerRecord() {
        return this.playerRecord;
    }

    public Player getPlayer() {
        return this.player;
    }
}
