package net.seocraft.api.shared.redis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class RedisChannel<O> implements Channel<O> {
    private String name;
    private TypeToken<O> type;

    private TypeToken<RedisWrapper<O>> wrappedType;

    private Supplier<Jedis> jedisSupplier;
    private JedisPubSub pubSub;

    private Gson gson;

    private Deque<ChannelListener<O>> channelListeners;

    private String serverChannelId = UUID.randomUUID().toString();

    RedisChannel(String name, TypeToken<O> type, Supplier<Jedis> redis, Gson gson, ExecutorService executorService) {
        this.name = name;
        this.type = type;

        jedisSupplier = redis;

        this.gson = gson;

        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                RedisWrapper<O> wrapper = gson.fromJson(message, new TypeToken<RedisWrapper<O>>() {
                }.getType());


                String id = wrapper.getId();

                if (id.equals(serverChannelId)) {
                    return;
                }

                O object = wrapper.getObject();

                channelListeners.forEach(listener -> {
                    listener.receiveMessage(object);
                });
            }
        };

        executorService.submit(() -> {
            jedisSupplier.get().subscribe(pubSub, name);
        });

        channelListeners = new ConcurrentLinkedDeque<>();

        wrappedType = new TypeToken<RedisWrapper<O>>() {
        };
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
        Jedis jedis = jedisSupplier.get();

        RedisWrapper<O> wrapper = new RedisWrapper<>(serverChannelId, object);

        String jsonRepresentation = gson.toJson(wrapper, wrappedType.getType());

        jedis.publish(name, jsonRepresentation);
    }

    @Override
    public void registerListener(ChannelListener<O> listener) {
        channelListeners.add(listener);
    }
}