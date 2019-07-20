package net.seocraft.commons.core.redis;

import com.google.inject.Inject;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.redis.messager.RedisClientConfiguration;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Singleton
public class SimpleRedisClient implements IRedisClient, RedisClient {

    private final RedisClientConfiguration config;
    private RedissonClient pool;

    @Inject
    SimpleRedisClient(RedisClientConfiguration config) {
        this.config = config;
    }

    @Override
    public RedissonClient getPool() {
        if (pool != null) {
            return pool;
        }

        String password = config.getPassword();

        Config redissonConfig = new Config()
                .setCodec(RedissonJacksonCodec.INSTANCE);

        SingleServerConfig serverConfig = redissonConfig.useSingleServer()
                .setAddress("redis://" + config.getAddress() + ":" + config.getPort());

        if (!password.trim().isEmpty()) {
            serverConfig.setPassword(password);
        }

        pool = Redisson.create(redissonConfig);

        return pool;
    }

    @Override
    public void setString(String key, String value) {
        getPool().<String>getBucket(key).set(value);
    }

    @Override
    public void deleteString(String key) {
        getPool().<String>getBucket(key).delete();
    }

    @Override
    public @NotNull String getLastStringElement(String key) {
        return getPool().<String>getQueue(key).poll();
    }

    @Override
    public @NotNull Map<String, String> getHashFields(String key) {
        return getPool().<String, String>getMap(key).readAllMap();
    }

    @Override
    public @NotNull String getString(String key) {
        return getPool().<String>getBucket(key).get();
    }

    @Override
    public void setLastStringElement(String key) {
        // do nothing
    }

    @Override
    public void setHash(String key, String field, @Nullable String value) {
        getPool().<String, String>getMap(key).put(field, value);
    }

    @Override
    public void deleteHash(String key, String field) {
        getPool().<String,String>getMap(key).remove(field);
    }

    @Override
    public boolean existsInSet(String key, String field) {
        return getPool().getSet(key).contains(field);
    }

    @Override
    public void addToSet(String key, String value) {
        getPool().getSet(key).add(value);
    }

    @Override
    public void removeFromSet(String key, String value) {
        getPool().getSet(key).remove(value);
    }

    @Override
    public void setExpiration(String key, Integer seconds) {
        getPool().getBucket(key).expire(seconds, TimeUnit.SECONDS);
    }

    @Override
    public void setExpiration(String key, long seconds) {
        getPool().getBucket(key).expire(seconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean existsKey(String key) {
        return getPool().getBucket(key).isExists();
    }

    @Override
    public @NotNull Set<String> getKeys(String pattern) {
        Set<String> keys = new HashSet<>();

        getPool().getKeys().getKeysByPattern(pattern).forEach(keys::add);

        return keys;
    }

    @Override
    public long getExpiringTime(String key) {
        return getPool().getBucket(key).remainTimeToLive();
    }

}
