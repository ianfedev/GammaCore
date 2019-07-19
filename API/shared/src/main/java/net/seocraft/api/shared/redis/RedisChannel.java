package net.seocraft.api.shared.redis;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;


import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class RedisChannel<O> implements Channel<O> {
    private String name;
    private TypeToken<O> type;

    private TypeToken<RedisWrapper<O>> wrappedType;

    private Supplier<StatefulRedisPubSubConnection<String, String>> jedisSupplier;
    private RedisPubSubListener<String, String> pubSub;

    private ExecutorService service;

    private Gson gson;

    private Deque<ChannelListener<O>> channelListeners;

    private String serverChannelId = UUID.randomUUID().toString();

    RedisChannel(String name, TypeToken<O> type, Supplier<StatefulRedisPubSubConnection<String, String>> redis, Gson gson, ExecutorService executorService) {
        this.name = name;
        this.type = type;

        jedisSupplier = redis;

        service = executorService;

        this.gson = gson;

        wrappedType = new TypeToken<RedisWrapper<O>>() {
        };

        wrappedType = (TypeToken<RedisWrapper<O>>) TypeToken.of(wrappedType.getType()).where(new TypeParameter<O>() {
        }, type);


        pubSub = new RedisPubSubAdapter<String, String>() {
            @Override
            public void message(String channel, String message) {
                if (!channel.equals(name)) {
                    return;
                }

                RedisWrapper<O> wrapper = gson.fromJson(message, wrappedType.getType());

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
            jedisSupplier.get().sync().subscribe(name);
        });

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
        service.submit(() -> {
            RedisWrapper<O> wrapper = new RedisWrapper<>(serverChannelId, object);

            String jsonRepresentation = gson.toJson(wrapper, wrappedType.getType());

            jedisSupplier.get().sync().publish(name, jsonRepresentation);
        });
    }

    @Override
    public void registerListener(ChannelListener<O> listener) {
        channelListeners.add(listener);
    }
}