package net.seocraft.api.bukkit.game.match;

import org.jetbrains.annotations.NotNull;

public interface MatchTimerProvider {

    void updateMatchRemaingTime(@NotNull Match match, int time);

    int getRemainingTime(@NotNull Match match);

    boolean hasRemainingTime(@NotNull Match match);

    void removeMatchTime(@NotNull Match match);

}
