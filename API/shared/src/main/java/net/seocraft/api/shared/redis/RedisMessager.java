package net.seocraft.api.shared.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedisMessager implements Messager {

    private Lock lock;

    private Map<String, TypeToken> registeredTypes;
    private Map<String, Channel> registeredChannels;

    private RedisClientConfiguration configuration;

    private Jedis jedis;

    private Gson gson;

    private ExecutorService executorService;

    @Inject
    RedisMessager(RedisClientConfiguration configuration, Gson gson, ExecutorService executorService) {
        this.lock = new ReentrantLock();

        registeredChannels = new HashMap<>();
        registeredTypes = new HashMap<>();

        this.configuration = configuration;

        this.gson = gson;

        this.executorService = executorService;
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

            Channel<O> channel = new RedisChannel<>(name, type, this, gson, executorService);

            registeredChannels.put(name, channel);
            registeredTypes.put(name, type);

            return channel;
        } finally {
            lock.unlock();
        }
    }

    Jedis getConnection() {
        lock.lock();

        try {
            if(jedis == null){
                jedis = new Jedis(configuration.getAddress(), configuration.getPort());
            }

            return jedis;
        } finally {
            lock.unlock();
        }
    }
}
