package net.seocraft.commons.bungee.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.commons.bungee.CommonsBungee;
import org.jetbrains.annotations.NotNull;

@Singleton
public class BungeeTokenQuery implements ServerTokenQuery {

    @Inject private CommonsBungee instance;
    @Inject private RedisClient redis;

    @Override
    public @NotNull String getToken() {
        return this.redis.getHashFields("authorization").get(this.instance.getServerRecord().getId());
    }

}
