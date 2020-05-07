package net.seocraft.api.bukkit.game.match;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface MatchDataProvider {

    @NotNull Set<User> getMatchParticipants(@NotNull Match match);

    @NotNull Set<User> getMatchParticipants(@NotNull Match match, @Nullable PlayerType type);

    @Nullable MatchAssignation getPlayerMatch(@NotNull String id);

}
