package net.seocraft.api.bukkit.game.match;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface MatchCacheManager {

    void cacheMatch(@NotNull Match match) throws JsonProcessingException;

    @NotNull Set<Match> getCachedMatches();

    void invalidateCache(@NotNull String match);

}
