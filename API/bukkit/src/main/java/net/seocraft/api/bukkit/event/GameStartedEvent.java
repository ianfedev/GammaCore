package net.seocraft.api.bukkit.event;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GameStartedEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    @NotNull private Match match;
    @NotNull private Set<Team> updatableTeam;

    public GameStartedEvent(@NotNull Match match, @NotNull Set<Team> updatableTeam) {
        this.match = match;
        this.updatableTeam = updatableTeam;
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

    public @NotNull Set<Team> getUpdatableTeam() {
        return this.updatableTeam;
    }
}