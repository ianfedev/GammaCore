package net.seocraft.api.shared.redis;

import com.google.inject.Inject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class RedisClientImpl implements IRedisClient {

    private final RedisClientConfiguration config;
    private StatefulRedisConnection<String, String> pool;

    @Inject
    RedisClientImpl(RedisClientConfiguration config) {
        this.config = config;
    }

    public StatefulRedisConnection<String, String> getPool() {
        RedisClient client = RedisClient.create("redis://" + this.config.getAddress() + ":" + this.config.getPort());
        if (this.pool == null) {
            this.pool = client.connect();
            return pool;
        } else {
            return this.pool;
        }

    }

    public void setString(String key, String value) {
        getPool().sync().set(key, value);
    }

    public void deleteString(String key) {
        getPool().sync().del(key);
    }

    public String getLastStringElement(String key) {
        return getPool().sync().lpop(key);
    }

    public Map<String, String> getHashFields(String key) {
        return getPool().sync().hgetall(key);
    }

    public String getString(String key) {
        return getPool().sync().get(key);
    }

    public void setLastStringElement(String key) {
        getPool().sync().lpush(key);
    }

    public void setHash(String key, String field, @Nullable String value) {
        getPool().sync().hset(key, field, value);
    }

    public void deleteHash(String key, String field) {
        getPool().sync().hdel(key, field);
    }

    @Override
    public boolean existsInHash(String key, String field) {
        return getPool().sync().sismember(key, field);
    }

    public void setExpiration(String key, Integer seconds) {
        getPool().sync().expire(key, seconds);
    }

    public void setExpiration(String key, long seconds) {
        getPool().sync().expire(key, seconds);

    }

    public Boolean existsKey(String key) {
        return getPool().sync().exists(key) > 0;
    }

    public Set<String> getKeys(String pattern) {
        return new HashSet<>(getPool().sync().keys(pattern));
    }

    public long getExpiringTime(String key) {
        return getPool().sync().ttl(key);
    }

}
