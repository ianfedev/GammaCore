package net.seocraft.api.shared.redis;

import com.google.inject.Inject;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Singleton
public class RedisClientImpl implements IRedisClient {

    private final RedisClientConfiguration config;
    private RedissonClient pool;

    @Inject
    RedisClientImpl(RedisClientConfiguration config) {
        this.config = config;
    }

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

    public void setString(String key, String value) {
        getPool().<String>getBucket(key).set(value);
    }

    public void deleteString(String key) {
        getPool().<String>getBucket(key).delete();
    }

    public String getLastStringElement(String key) {
        return getPool().<String>getQueue(key).poll();
    }

    public Map<String, String> getHashFields(String key) {
        return getPool().<String, String>getMap(key).readAllMap();
    }

    public String getString(String key) {
        return getPool().<String>getBucket(key).get();
    }

    public void setLastStringElement(String key) {
        // do nothing
    }

    public void setHash(String key, String field, @Nullable String value) {
        getPool().<String, String>getMap(key).put(field, value);
    }

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

    public void setExpiration(String key, Integer seconds) {
        getPool().getBucket(key).expire(seconds, TimeUnit.SECONDS);
    }

    public void setExpiration(String key, long seconds) {
        getPool().getBucket(key).expire(seconds, TimeUnit.SECONDS);
    }

    public Boolean existsKey(String key) {
        return getPool().getBucket(key).isExists();
    }

    public Set<String> getKeys(String pattern) {
        Set<String> keys = new HashSet<>();

        getPool().getKeys().getKeysByPattern(pattern).forEach(keys::add);

        return keys;
    }

    public long getExpiringTime(String key) {
        return getPool().getBucket(key).remainTimeToLive();
    }

}
