package net.seocraft.api.core;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.old.online.OnlinePlayersImpl;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.redis.SimpleRedisClient;
import net.seocraft.api.core.redis.messager.RedisMessager;
import net.seocraft.api.core.old.serialization.model.ModelSerializationHandler;
import net.seocraft.api.core.old.serialization.model.ModelSerializationHandlerImp;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.session.SessionHandlerImp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedModule extends AbstractModule {
    @Override
    protected void configure() {
        /*bind(ExecutorService.class).to(ListeningExecutorService.class);
        bind(ListeningExecutorService.class).toInstance(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8)));
        bind(OnlineStatusManager.class).to(OnlinePlayersImpl.class);
        bind(IRedisClient.class).to(SimpleRedisClient.class);
        bind(ModelSerializationHandler.class).to(ModelSerializationHandlerImp.class);
        bind(GameSessionManager.class).to(SessionHandlerImp.class);
        bind(Messager.class).to(RedisMessager.class).in(Scopes.SINGLETON);*/
    }
}