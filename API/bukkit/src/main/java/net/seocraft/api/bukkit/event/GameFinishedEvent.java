package net.seocraft.api.bukkit.event;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameFinishedEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    @NotNull private Match match;
    @NotNull private Player player;
    @NotNull private User user;
    @Nullable private Team winnerTeam;

    public GameFinishedEvent(@NotNull Match match, @NotNull Player player, @NotNull User user, @Nullable Team winnerTeam) {
        this.match = match;
        this.player = player;
        this.user = user;
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

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull User getUser() {
        return this.user;
    }

    public @Nullable Team getWinnerTeam() {
        return winnerTeam;
    }
}
