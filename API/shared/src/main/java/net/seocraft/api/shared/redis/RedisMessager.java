package net.seocraft.api.shared.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedisMessager implements Messager {

    private Lock lock;

    private Map<String, TypeToken> registeredTypes;
    private Map<String, Channel> registeredChannels;

    private RedisClient client;
    private Gson gson;

    @Inject
    RedisMessager(RedisClient client, Gson gson) {
        this.lock = new ReentrantLock();

        registeredChannels = new HashMap<>();
        registeredTypes = new HashMap<>();

        this.client = client;
        this.gson = gson;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> Channel<O> getChannel(String name, TypeToken<O> type) {
        try {
            lock.lock();

            if (registeredChannels.containsKey(name)) {
                if (registeredTypes.get(name) != type) {
                    throw new IllegalStateException("A channel with the name " + name + " is already registered with another type, type: " + registeredTypes.get(name).getType().getTypeName());
                }

                return registeredChannels.get(name);
            }

            Channel<O> channel = new RedisChannel<>(name, type, this.client.getPool(), gson);

            registeredChannels.put(name, channel);
            registeredTypes.put(name, type);

            return channel;
        } finally {
            lock.unlock();
        }
    }
}
