package net.seocraft.api.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MatchUpdateEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    @NotNull private String match;

    public MatchUpdateEvent(@NotNull String match) {
        this.match = match;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull String getMatch() {
        return this.match;
    }


}
