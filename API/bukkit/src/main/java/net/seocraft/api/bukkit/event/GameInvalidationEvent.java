package net.seocraft.api.bukkit.event;

import net.seocraft.api.bukkit.game.match.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameInvalidationEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    @NotNull private Match match;

    public GameInvalidationEvent(@NotNull Match match) {
        this.match = match;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull Match getMatch() {
        return this.match;
    }

}
