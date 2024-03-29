package net.seocraft.api.core.redis.messager;

import com.google.common.reflect.TypeToken;

public interface Messager {

    <O> Channel<O> getChannel(String name, TypeToken<O> type);

    default <O> Channel<O> getChannel(String name, Class<O> type) {
        return getChannel(name, TypeToken.of(type));
    }

}
