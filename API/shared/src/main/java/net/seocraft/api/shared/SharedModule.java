package net.seocraft.api.shared;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.seocraft.api.shared.redis.Messager;
import net.seocraft.api.shared.redis.RedisMessager;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.Executors;

public class SharedModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ListeningExecutorService.class).toInstance(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8)));
        bind(Gson.class).toProvider(() -> {
            return new GsonBuilder()
                    .serializeNulls()
                    .enableComplexMapKeySerialization()
                    .setPrettyPrinting()
                    .create();
        }).in(Scopes.SINGLETON);

        requireBinding(JedisPool.class);
        bind(Messager.class).to(RedisMessager.class).in(Scopes.SINGLETON);
    }
}