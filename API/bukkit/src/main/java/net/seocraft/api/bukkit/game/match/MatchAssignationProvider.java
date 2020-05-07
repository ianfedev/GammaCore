package net.seocraft.api.bukkit.game.match;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface MatchAssignationProvider {

    @NotNull Map<String, PlayerType> getMatchAssignations(@NotNull String id);

    @NotNull Match setMatchAssignation(@NotNull Match match);

    void assignPlayer(@NotNull String id, @NotNull Match match, @NotNull PlayerType type);

    void unassignPlayer(@NotNull Match match, @NotNull String id);

}
