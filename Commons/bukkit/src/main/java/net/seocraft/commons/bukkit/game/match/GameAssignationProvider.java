package net.seocraft.commons.bukkit.game.match;

import com.google.common.base.Enums;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchAssignationProvider;
import net.seocraft.api.bukkit.game.match.PlayerType;
import net.seocraft.api.core.redis.RedisClient;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GameAssignationProvider implements MatchAssignationProvider {

    @Inject private RedisClient redisClient;

    @Override
    public @NotNull Map<String, PlayerType> getMatchAssignations(@NotNull String id) {
        Map<String, PlayerType> assignations = new HashMap<>();
        for (Map.Entry<String, String> entry : this.redisClient.getHashFields("match:" + id).entrySet()) {
            assignations.put(entry.getKey(), Enums.getIfPresent(PlayerType.class, entry.getValue()).or(PlayerType.SPECTATOR));
        }
        return assignations;
    }

    @Override
    public @NotNull Match setMatchAssignation(@NotNull Match match) {
        match.setMatchRecord(getMatchAssignations(match.getId()));
        return match;
    }

    @Override
    public void assignPlayer(@NotNull String id, @NotNull Match match, @NotNull PlayerType type) {
        this.redisClient.setHash("match:" + id, id, type.toString());
    }

    @Override
    public void unassignPlayer(@NotNull Match match, @NotNull String id) {
        this.redisClient.deleteHash("match:" + id, id);
    }

    @Override
    public @NotNull Match clearMatchAssignations(@NotNull Match match) {
        this.redisClient.deleteString("match:" + match.getId());
        match.getMatchRecord().clear();
        return match;
    }

}
