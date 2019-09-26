package net.seocraft.api.bukkit.event;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameSpectatorSetEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    @NotNull private Match match;
    @NotNull private User user;
    @NotNull private Player player;
    private boolean custom;
    private boolean manual;

    public GameSpectatorSetEvent(@NotNull Match match, @NotNull User user, @NotNull Player player, boolean custom, boolean manual) {
        this.match = match;
        this.user = user;
        this.player = player;
        this.custom = custom;
        this.manual = manual;
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

    public @NotNull User getUser() {
        return this.user;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public boolean isCustom() {
        return this.custom;
    }

    public boolean isManual() {
        return this.manual;
    }
}
