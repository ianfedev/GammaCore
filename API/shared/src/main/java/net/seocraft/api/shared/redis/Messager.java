package net.seocraft.api.shared.redis;

import com.google.gson.reflect.TypeToken;

public interface Messager {
    <O> Channel<O> getChannel(String name, TypeToken<O> type);

    default <O> Channel<O> getChannel(String name, Class<O> type) {
        return getChannel(name, TypeToken.get(type));
    }

}
