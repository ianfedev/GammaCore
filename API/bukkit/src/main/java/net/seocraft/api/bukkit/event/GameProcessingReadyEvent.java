package net.seocraft.api.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameProcessingReadyEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    @NotNull private String gamemode;
    @NotNull private String subGamemode;
    private int allowedStarts;

    public GameProcessingReadyEvent(@NotNull String gamemode, @NotNull String subGamemode, int allowedStarts) {
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.allowedStarts = allowedStarts;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull String getGamemode() {
        return this.gamemode;
    }

    public @NotNull String getSubGamemode() {
        return this.subGamemode;
    }

    public int getAllowedStarts() {
        return this.allowedStarts;
    }
}
