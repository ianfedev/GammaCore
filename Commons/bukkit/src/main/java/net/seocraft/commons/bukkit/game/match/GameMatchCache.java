package net.seocraft.commons.bukkit.game.match;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchAssignationProvider;
import net.seocraft.api.bukkit.game.match.MatchCacheManager;
import net.seocraft.api.core.redis.RedisClient;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GameMatchCache implements MatchCacheManager {

    @Inject private RedisClient redisClient;
    @Inject private MatchAssignationProvider matchAssignationProvider;
    @Inject private ObjectMapper objectMapper;

    @Override
    public void cacheMatch(@NotNull Match match) throws JsonProcessingException {
        this.redisClient.setString("matchCache:" + match.getId(), objectMapper.writeValueAsString(match));
        this.redisClient.setExpiration("matchCache:" + match.getId(), 120);
    }

    @Override
    public @NotNull Set<Match> getCachedMatches() {
        return this.redisClient.getKeys("matchCache:*").stream()
                .map(key -> {
                    Match baseMatch;
                    try {
                        baseMatch = this.objectMapper.readValue(
                                this.redisClient.getString(key),
                                Match.class
                        );
                        return this.matchAssignationProvider.setMatchAssignation(baseMatch);
                    } catch (IOException ex) {
                        Bukkit.getLogger().log(Level.WARNING, "Invalidated cache due to malforming while parsing.", ex);
                        redisClient.deleteString(key);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public void invalidateCache(@NotNull String match) {
        this.redisClient.deleteString("matchCache:" + match);
    }

}
