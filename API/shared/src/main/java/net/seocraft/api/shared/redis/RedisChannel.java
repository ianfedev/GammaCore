package net.seocraft.api.shared.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RedisChannel<O> implements Channel<O> {
    private String name;
    private TypeToken<O> type;

    private JedisPool pool;
    private JedisPubSub pubSub;

    private Gson gson;

    private Deque<ChannelListener<O>> channelListeners;

    RedisChannel(String name, TypeToken<O> type, JedisPool pool, Gson gson) {
        this.name = name;
        this.type = type;

        this.pool = pool;

        this.gson = gson;

        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                O object = gson.fromJson(message, type.getType());

                channelListeners.forEach(listener -> {
                    listener.receiveMessage(object);
                });
            }
        };

        try (Jedis jedis = pool.getResource()) {
            jedis.subscribe(pubSub, name);
        }

        channelListeners = new ConcurrentLinkedDeque<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeToken<O> getType() {
        return type;
    }

    @Override
    public void sendMessage(O object) {
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(name, gson.toJson(object));
        }
    }

    @Override
    public void registerListener(ChannelListener<O> listener) {
        channelListeners.add(listener);
    }
}
