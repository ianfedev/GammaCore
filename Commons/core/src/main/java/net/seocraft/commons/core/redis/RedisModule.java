package net.seocraft.commons.core.redis;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.commons.core.redis.messager.RedisMessager;

public class RedisModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(RedisClient.class).to(SimpleRedisClient.class);
        bind(Messager.class).to(RedisMessager.class);
    }

}
