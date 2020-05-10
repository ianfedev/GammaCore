package net.seocraft.commons.bukkit.game.gamemode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeCache;
import net.seocraft.api.core.redis.RedisClient;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CoreGamemodeCache implements GamemodeCache {

    @Inject private RedisClient redisClient;
    @Inject private ObjectMapper objectMapper;

    @Override
    public void cacheMatch(@NotNull Gamemode gamemode) throws JsonProcessingException {
        this.redisClient.setString("gamemode:" + gamemode.getId(), objectMapper.writeValueAsString(gamemode));
        this.redisClient.setExpiration("gamemode:" + gamemode.getId(), 120);
    }

    @Override
    public @NotNull Set<Gamemode> getCachedGamemodes() {
        return this.redisClient.getKeys("gamemode:*").stream()
                .map(key -> {
                    try {
                        return this.objectMapper.readValue(
                                this.redisClient.getString(key),
                                Gamemode.class
                        );
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
    public void invalidateCache(@NotNull String gamemode) {
        this.redisClient.deleteString("matchCache:" + gamemode);
    }

}
