package net.seocraft.commons.bukkit.game.match;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchAssignation;
import net.seocraft.api.bukkit.game.match.PlayerType;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class GameMatchAssignation implements MatchAssignation {

    private @NotNull final Match match;
    private @NotNull final PlayerType playerType;

    @ConstructorProperties({"match", "playerType"})
    public GameMatchAssignation(@NotNull Match match, @NotNull PlayerType playerType) {
        this.match = match;
        this.playerType = playerType;
    }

    @Override
    public @NotNull Match getMatch() {
        return this.match;
    }

    @Override
    public @NotNull PlayerType getPlayerType() {
        return this.playerType;
    }
}
