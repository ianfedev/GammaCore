package net.seocraft.api.shared.redis;

import com.google.gson.reflect.TypeToken;

public interface Channel<O> {
    String getName();

    TypeToken<O> getType();

    void sendMessage(O object);

    void registerListener(ChannelListener<O> listener);
}
