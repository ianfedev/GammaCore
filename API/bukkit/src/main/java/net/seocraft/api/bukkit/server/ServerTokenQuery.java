package net.seocraft.api.bukkit.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.shared.redis.RedisClient;

@Singleton
public class ServerTokenQuery {

    private BukkitAPI instance = BukkitAPI.getInstance();
    private RedisClient redis;

    @Inject ServerTokenQuery(RedisClient redis) {
        this.redis = redis;
    }

    public String getToken() {
        return this.redis.getHashFields("authorization").get(instance.getServerRecord().getSlug());
    }
}
