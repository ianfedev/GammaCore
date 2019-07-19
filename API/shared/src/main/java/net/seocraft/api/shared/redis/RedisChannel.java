package net.seocraft.api.shared.redis;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListeningExecutorService;
import lombok.Getter;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RedisChannel<T> implements Channel<T> {

    @Getter
    private String name;
    @Getter
    private TypeToken<T> type;

    private TypeToken<ObjectWrapper<T>> wrappedType;

    private Queue<RedissonChannelWrapper<T>> registeredListeners;

    private RedissonClient redisson;
    private ListeningExecutorService executorService;

    private String uniqueId = UUID.randomUUID().toString();

    RedisChannel(String channelName, TypeToken<T> type, RedissonClient pool, ListeningExecutorService executorService) {
        this.name = channelName;
        this.type = type;

        this.redisson = pool;
        this.executorService = executorService;

        this.registeredListeners = new ConcurrentLinkedDeque<>();

        wrappedType = new TypeToken<ObjectWrapper<T>>(){}
        .where(new TypeParameter<T>() {}, type);
    }


    @Override
    public void registerListener(ChannelListener<T> listener) {
        Objects.requireNonNull(listener, "ChannelListener must be not null");

        RedissonChannelWrapper<T> wrapper = new RedissonChannelWrapper<>(name, uniqueId, listener);

        RTopic rTopic = redisson.getTopic(name);
        rTopic.addListener(wrappedType.getRawType(), wrapper);

        registeredListeners.offer(wrapper);
    }

    @Override
    public void sendMessage(T data) {
        executorService.submit(() -> {
            RTopic rTopic = redisson.getTopic(name);

            rTopic.publish(new ObjectWrapper<>(data, uniqueId));
        });

    }


}
