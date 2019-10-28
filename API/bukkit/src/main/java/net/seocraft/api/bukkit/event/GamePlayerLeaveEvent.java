package net.seocraft.api.bukkit.event;

import net.seocraft.api.core.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class GamePlayerLeaveEvent extends Event {

    private @NotNull User leftUser;
    private @NotNull Date joinedAt;
    private static HandlerList handlerList = new HandlerList();

    public GamePlayerLeaveEvent(@NotNull User leftUser) {
        this.leftUser = leftUser;
        this.joinedAt = new Date();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull User getLeftUser() {
        return leftUser;
    }

    public @NotNull Date getJoinedAt() {
        return joinedAt;
    }
}