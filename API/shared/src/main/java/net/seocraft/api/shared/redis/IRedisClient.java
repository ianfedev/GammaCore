package net.seocraft.api.shared.redis;

import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.Set;

public interface IRedisClient {

    RedissonClient getPool();

    String getString(String key);

    String getLastStringElement(String prefix);

    Map<String, String> getHashFields(String key);

    void setString(String key, String value);

    void deleteString(String key);

    void setLastStringElement(String prefix);

    void setExpiration(String key, Integer seconds);

    void setHash(String key, String field, String value);

    void deleteHash(String key, String field);

    boolean existsInSet(String key, String field);

    void addToSet(String key, String value);

    void removeFromSet(String key, String value);

    Boolean existsKey(String key);

    Set<String> getKeys(String pattern);

    long getExpiringTime(String key);

}