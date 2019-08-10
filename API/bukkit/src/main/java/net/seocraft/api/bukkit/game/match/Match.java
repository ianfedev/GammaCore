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

    @NotNull Set<String> getWinner();

    @NotNull String getGamemode();

    @NotNull String getSubGamemode();

}
