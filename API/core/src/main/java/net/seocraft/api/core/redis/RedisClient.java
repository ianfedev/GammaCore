package net.seocraft.api.core.redis;

import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public interface RedisClient {
    RedissonClient getPool();

    void setString(String key, String value);

    void deleteString(String key);

    @NotNull String getLastStringElement(String key);

    @NotNull Map<String, String> getHashFields(String key);

    @NotNull String getString(String key);

    void setLastStringElement(String key);

    void setHash(String key, String field, @Nullable String value);

    void deleteHash(String key, String field);

    boolean existsInSet(String key, String field);

    void addToSet(String key, String value);

    void removeFromSet(String key, String value);

    void setExpiration(String key, Integer seconds);

    void setExpiration(String key, long seconds);

    boolean existsKey(String key);

    @NotNull Set<String> getKeys(String pattern);

    long getExpiringTime(String key);
}
