package net.seocraft.api.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GamePairingEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
