package net.seocraft.api.shared.redis;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;


import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class RedisChannel<O> implements Channel<O> {

    private String name;
    private TypeToken<O> type;

    private TypeToken<RedisWrapper<O>> wrappedType;

    private Supplier<RedissonClient> redisSupplier;

    private ExecutorService service;


    private Deque<ChannelListener<O>> channelListeners;

    private String serverChannelId = UUID.randomUUID().toString();

    RedisChannel(String name, TypeToken<O> type, Supplier<RedissonClient> redis, ExecutorService executorService) {
        this.name = name;
        this.type = type;

        redisSupplier = redis;

        service = executorService;

        wrappedType = new TypeToken<RedisWrapper<O>>() {
        };

        wrappedType = (TypeToken<RedisWrapper<O>>) TypeToken.of(wrappedType.getType()).where(new TypeParameter<O>() {
        }, type);

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
        service.submit(() ->
            redisSupplier.get().getTopic(name).publish(new RedisWrapper<>(serverChannelId, object))
        );
    }

    @Override
    public void registerListener(ChannelListener<O> listener) {
        RTopic topic = redisSupplier.get().getTopic(name);

        topic.addListener(wrappedType.getRawType(), new RedissonWrapper<>(listener, serverChannelId));
        channelListeners.add(listener);
    }
}