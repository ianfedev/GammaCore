package net.seocraft.api.bukkit.event;

import net.seocraft.api.core.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class GamePlayerJoinEvent extends Event {

    private @NotNull User joinedUser;
    private @NotNull Date joinedAt;
    private static HandlerList handlerList = new HandlerList();

    public GamePlayerJoinEvent(@NotNull User joinedUser) {
        this.joinedUser = joinedUser;
        this.joinedAt = new Date();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull User getJoinedUser() {
        return joinedUser;
    }

    public @NotNull Date getJoinedAt() {
        return joinedAt;
    }

}