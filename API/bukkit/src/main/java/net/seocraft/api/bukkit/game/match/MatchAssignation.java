package net.seocraft.api.bukkit.game.match;

import org.jetbrains.annotations.NotNull;

public interface MatchAssignation {

    @NotNull Match getMatch();

    @NotNull PlayerType getPlayerType();

}
