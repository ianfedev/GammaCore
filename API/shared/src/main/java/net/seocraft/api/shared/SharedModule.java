package net.seocraft.api.shared;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.seocraft.api.shared.online.OnlinePlayersApi;
import net.seocraft.api.shared.online.OnlinePlayersImpl;
import net.seocraft.api.shared.redis.IRedisClient;
import net.seocraft.api.shared.redis.Messager;
import net.seocraft.api.shared.redis.RedisClientImpl;
import net.seocraft.api.shared.redis.RedisMessager;
import net.seocraft.api.shared.serialization.model.ModelSerializationHandler;
import net.seocraft.api.shared.serialization.model.ModelSerializationHandlerImp;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.session.SessionHandlerImp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExecutorService.class).to(ListeningExecutorService.class);
        bind(ListeningExecutorService.class).toInstance(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8)));
        bind(OnlinePlayersApi.class).to(OnlinePlayersImpl.class);
        bind(IRedisClient.class).to(RedisClientImpl.class);
        bind(ModelSerializationHandler.class).to(ModelSerializationHandlerImp.class);
        bind(SessionHandler.class).to(SessionHandlerImp.class);
        bind(Messager.class).to(RedisMessager.class).in(Scopes.SINGLETON);
    }
}