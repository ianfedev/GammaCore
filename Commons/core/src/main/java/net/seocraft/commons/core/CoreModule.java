package net.seocraft.commons.core;

import com.google.inject.AbstractModule;
import net.seocraft.commons.core.cooldown.CooldownModule;
import net.seocraft.commons.core.online.OnlineStatusModule;
import net.seocraft.commons.core.redis.RedisModule;
import net.seocraft.commons.core.server.ServerModule;
import net.seocraft.commons.core.session.SessionModule;
import net.seocraft.commons.core.user.UserModule;

public class CoreModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new CooldownModule());
        install(new OnlineStatusModule());
        install(new RedisModule());
        install(new ServerModule());
        install(new SessionModule());
        install(new UserModule());
    }

}
