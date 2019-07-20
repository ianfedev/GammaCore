package net.seocraft.commons.core.redis.messager;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;

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

    private RedisClient redisClient;

    private ExecutorService executorService;

    @Inject
    RedisMessager(RedisClient client, ExecutorService executorService) {
        this.lock = new ReentrantLock();

        registeredChannels = new HashMap<>();
        registeredTypes = new HashMap<>();

        redisClient = client;

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

            Channel<O> channel = new RedisChannel<>(name, type, redisClient.getPool(), executorService);

            registeredChannels.put(name, channel);
            registeredTypes.put(name, type);

            return channel;
        } finally {
            lock.unlock();
        }
    }

}
