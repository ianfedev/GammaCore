package net.seocraft.api.core.redis.messager;

import com.google.common.reflect.TypeToken;

public interface Channel<O> {
    String getName();

    TypeToken<O> getType();

    void sendMessage(O object);

    void registerListener(ChannelListener<O> listener);
}
