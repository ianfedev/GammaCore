package net.seocraft.api.shared.redis;

import io.lettuce.core.api.StatefulRedisConnection;

import java.util.Map;
import java.util.Set;

public interface IRedisClient {


    StatefulRedisConnection<String, String> getPool();

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

    Boolean existsKey(String key);

    Set<String> getKeys(String pattern);

    long getExpiringTime(String key);

}