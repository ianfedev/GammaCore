package net.seocraft.api.shared.redis;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.lettuce.core.RedisClient;

import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class RedisMessager implements Messager {

    private Lock lock;

    private Map<String, TypeToken> registeredTypes;
    private Map<String, Channel> registeredChannels;

    private RedisClientConfiguration config;

    private StatefulRedisPubSubConnection<String, String> redis;

    private Gson gson;

    private ExecutorService executorService;

    @Inject
    RedisMessager(RedisClientConfiguration configuration, Gson gson, ExecutorService executorService) {
        this.lock = new ReentrantLock();

        registeredChannels = new HashMap<>();
        registeredTypes = new HashMap<>();

        config = configuration;

        this.gson = gson;

        this.executorService = executorService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> Channel<O> getChannel(String name, TypeToken<O> type) {
        try {
            lock.lock();

            if (registeredChannels.containsKey(name)) {
                if (!registeredTypes.get(name).equals(type)) {
                    throw new IllegalStateException("A channel with the name " + name + " is already registered with another type, type: " + registeredTypes.get(name).getType().getTypeName());
                }

                return registeredChannels.get(name);
            }

            Channel<O> channel = new RedisChannel<>(name, type, this::getConnection, gson, executorService);

            registeredChannels.put(name, channel);
            registeredTypes.put(name, type);

            return channel;
        } finally {
            lock.unlock();
        }
    }

    StatefulRedisPubSubConnection<String, String> getConnection() {
        lock.lock();

        RedisClient client = RedisClient.create("redis://" + this.config.getAddress() + ":" + this.config.getPort());

        try {
            if (redis == null) {
                redis = client.connectPubSub();
            }

            return redis;
        } finally {
            lock.unlock();
        }
    }
}
