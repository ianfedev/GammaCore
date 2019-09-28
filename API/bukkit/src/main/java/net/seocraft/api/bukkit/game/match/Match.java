package net.seocraft.api.bukkit.game.match;

import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Match extends Model {

    @NotNull String getMap();

    long getCreatedAt();

    @NotNull Set<Team> getTeams();

    @NotNull MatchStatus getStatus();

    void setStatus(@NotNull MatchStatus status);

    void setTeams(@NotNull Set<Team> teams);

    @NotNull Set<String> getWinner();

    void setWinner(@NotNull Set<String> winner);

    @NotNull String getGamemode();

    @NotNull String getSubGamemode();

}
