package net.seocraft.commons.bukkit.game.match;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.PlayerType;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameMatch implements Match {

    @NotNull private String id;
    @NotNull private String map;
    private long createdAt;
    @NotNull private Set<Team> teams;
    @NotNull private MatchStatus status;
    @NotNull private Set<String> winner;
    @NotNull private String gamemode;
    @NotNull private String subGamemode;
    private Map<String, PlayerType> matchAssignations;

    @ConstructorProperties({"_id", "map", "createdAt", "teams", "status", "winner", "gamemode", "subGamemode"})
    public GameMatch(@NotNull String id, @NotNull String map, long createdAt, @NotNull Set<Team> teams, @NotNull MatchStatus status, @NotNull Set<String> winner, @NotNull String gamemode, @NotNull String subGamemode) {
        this.id = id;
        this.map = map;
        this.createdAt = createdAt;
        this.teams = teams;
        this.status = status;
        this.winner = winner;
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.matchAssignations = new HashMap<>();
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public @NotNull String getMap() {
        return this.map;
    }

    @Override
    public long getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public @NotNull Set<Team> getTeams() {
        return this.teams;
    }

    @Override
    public @NotNull MatchStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(@NotNull MatchStatus status) {
        this.status = status;
    }

    @Override
    public void setTeams(@NotNull Set<Team> teams) {
        this.teams = teams;
    }

    @Override
    public @NotNull Set<String> getWinner() {
        return this.winner;
    }

    @Override
    public void setWinner(@NotNull Set<String> winner) {
        this.winner = winner;
    }

    @Override
    public @NotNull String getGamemode() {
        return this.gamemode;
    }

    @Override
    public @NotNull String getSubGamemode() {
        return this.subGamemode;
    }

    @Override
    public void addPlayer(@NotNull User user, @NotNull PlayerType playerType) {
        this.matchAssignations.put(user.getId(), playerType);
    }

    @Override
    public void removePlayer(@NotNull User user) {
        this.matchAssignations.remove(user.getId());
    }

    @Override
    public @NotNull Map<String, PlayerType> getMatchRecord() {
        return this.matchAssignations;
    }

    @Override
    public void setMatchRecord(@NotNull Map<String, PlayerType> record) {
        if (this.matchAssignations.isEmpty()) this.matchAssignations = record;
    }
}
