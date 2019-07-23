package net.seocraft.commons.core;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.commons.core.cooldown.CooldownModule;
import net.seocraft.commons.core.online.OnlineStatusModule;
import net.seocraft.commons.core.redis.RedisModule;
import net.seocraft.commons.core.server.CoreServerModule;
import net.seocraft.commons.core.session.SessionModule;
import net.seocraft.commons.core.user.CoreUserModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(ListeningExecutorService.class).toInstance(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8)));
        bind(ExecutorService.class).to(ListeningExecutorService.class);
        install(new CooldownModule());
        install(new OnlineStatusModule());
        install(new RedisModule());
        install(new CoreServerModule());
        install(new SessionModule());
        install(new CoreUserModule());
    }

}
