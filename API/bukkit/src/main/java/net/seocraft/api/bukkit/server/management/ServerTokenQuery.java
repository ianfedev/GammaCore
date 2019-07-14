package net.seocraft.api.bukkit.server.management;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.shared.redis.RedisClient;

@Singleton
public class ServerTokenQuery {

    @Inject private BukkitAPI instance;
    @Inject private RedisClient redis;

    public String getToken() {
        return this.redis.getHashFields("authorization").get(this.instance.getServerRecord().id());
    }
}
