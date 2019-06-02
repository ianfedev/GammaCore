package net.seocraft.api.shared.redis;

import com.google.inject.Inject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class RedisClient implements IRedisClient {

    private final RedisClientConfiguration config;
    private JedisPool pool;

    @Inject
    RedisClient(RedisClientConfiguration config) {
        this.config = config;
    }

    public JedisPool getPool() {
        if (this.pool == null) {
            return new JedisPool(this.config.getAddress(), this.config.getPort());
        } else {
            return this.pool;
        }
    }

    public void setString(String key, String value) {
        try (Jedis client = getPool().getResource()) {
            //TODO: Fix password/database issue
            /*client.auth(this.config.getPassword());
            client.select(this.config.getDatabase());*/
            client.set(key, value);
        }
    }

    public void deleteString(String key) {
        try (Jedis client = getPool().getResource()) {
            client.del(key);
        }
    }

    public String getLastStringElement(String key) {
        try (Jedis client = getPool().getResource()) {
            return client.lpop(key);
        }
    }

    public Map<String, String> getHashFields(String key) {
        try (Jedis client = getPool().getResource()) {
            return client.hgetAll(key);
        }
    }

    public String getString(String key) {
        try (Jedis client = getPool().getResource()) {
            return client.get(key);
        }
    }

    public void setLastStringElement(String key) {
        try (Jedis client = getPool().getResource()) {
            client.lpush(key);
        }
    }

    public void setHash(String key, String field, @Nullable String value) {
        try (Jedis client = getPool().getResource()) {
            client.hset(key, field, value);
        }
    }

    public void deleteHash(String key, String field) {
        try (Jedis client = getPool().getResource()) {
            client.hdel(key, field);
        }
    }

    public void setExpiration(String key, Integer seconds) {
        try (Jedis client = getPool().getResource()) {
            client.expire(key, seconds);
        }
    }

    public Boolean existsKey(String key) {
        try (Jedis client = getPool().getResource()) {
            return client.exists(key);
        }
    }

}
