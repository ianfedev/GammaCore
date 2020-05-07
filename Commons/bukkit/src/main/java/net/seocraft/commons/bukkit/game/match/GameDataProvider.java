package net.seocraft.commons.bukkit.game.match;

import com.google.common.base.Enums;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.match.*;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GameDataProvider implements MatchDataProvider {

    @Inject private MatchAssignationProvider matchAssignationProvider;
    @Inject private MatchProvider matchProvider;
    @Inject private RedisClient redisClient;
    @Inject private UserStorageProvider userStorageProvider;

    @Override
    public @NotNull Set<User> getMatchParticipants(@NotNull Match match) {
        return getMatchParticipants(match, null);
    }

    @Override
    public @NotNull Set<User> getMatchParticipants(@NotNull Match match, @Nullable PlayerType type) {
        return this.matchAssignationProvider.getMatchAssignations(match.getId())
                .entrySet()
                .stream()
                .filter(e -> type == null || e.getValue() == PlayerType.HOLDING)
                .map(entry -> {
                    try {
                        return this.userStorageProvider.getCachedUserSync(entry.getKey());
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ex) {
                        Bukkit.getLogger().log(Level.WARNING, "There was an error while retreiving assignated cache.", ex);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public @Nullable MatchAssignation getPlayerMatch(@NotNull String id) {
        for (String key : this.redisClient.getKeys("match:*")) {
            for (Map.Entry<String, String> entry : this.redisClient.getHashFields(key).entrySet()) {
                if (entry.getKey().equalsIgnoreCase(id)) {
                    try {
                        return new GameMatchAssignation(
                                this.matchProvider.getCachedMatchSync(key.replace("match:", "")),
                                Enums.getIfPresent(PlayerType.class, entry.getValue()).or(PlayerType.SPECTATOR)
                        );
                    } catch (IOException | Unauthorized | BadRequest | NotFound | InternalServerError e) {
                        Bukkit.getLogger().log(Level.WARNING, "There was an error while parsing match assignation", e);
                    }
                }
            }
        }
        return null;
    }

}
