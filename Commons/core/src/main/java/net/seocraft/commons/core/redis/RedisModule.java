package net.seocraft.commons.core.redis;

import com.google.inject.AbstractModule;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.commons.core.redis.messager.RedisMessager;

public class RedisModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RedisClient.class).to(SimpleRedisClient.class);
        bind(Messager.class).to(RedisMessager.class);
    }

}
