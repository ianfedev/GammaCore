package net.seocraft.commons.core;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.cooldown.CooldownManager;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.core.cooldown.CoreCooldownManager;
import net.seocraft.commons.core.online.CraftOnlineStatusManager;
import net.seocraft.commons.core.redis.RedisModule;
import net.seocraft.commons.core.server.CoreServerManager;
import net.seocraft.commons.core.user.GammaUserStorageProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreModule extends ProtectedModule {

    @Override
    protected void configure() {
        install(new RedisModule());
        bind(ListeningExecutorService.class).toInstance(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8)));
        bind(ExecutorService.class).to(ListeningExecutorService.class);
        bind(CooldownManager.class).to(CoreCooldownManager.class);
        bind(OnlineStatusManager.class).to(CraftOnlineStatusManager.class);
        bind(ServerManager.class).to(CoreServerManager.class).in(Scopes.SINGLETON);
        bind(UserStorageProvider.class).to(GammaUserStorageProvider.class).in(Scopes.SINGLETON);
        expose(ListeningExecutorService.class);
        expose(ExecutorService.class);
        expose(CooldownManager.class);
        expose(OnlineStatusManager.class);
        expose(ServerManager.class);
        expose(UserStorageProvider.class);
    }

}
