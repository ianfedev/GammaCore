package net.seocraft.api.bukkit.event;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameFinishedEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    @NotNull private Match match;
    @Nullable private Team winnerTeam;

    public GameFinishedEvent(@NotNull Match match, @Nullable Team winnerTeam) {
        this.match = match;
        this.winnerTeam = winnerTeam;
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

    public @Nullable Team getWinnerTeam() {
        return winnerTeam;
    }
}
