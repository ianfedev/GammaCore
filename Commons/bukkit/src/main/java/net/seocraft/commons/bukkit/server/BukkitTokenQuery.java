package net.seocraft.commons.bukkit.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.jetbrains.annotations.NotNull;

@Singleton
public class BukkitTokenQuery implements ServerTokenQuery {

    @Inject private CommonsBukkit instance;
    @Inject private RedisClient redis;

    public @NotNull String getToken() {
        return this.redis.getHashFields("authorization").get(this.instance.getServerRecord().id());
    }
}
