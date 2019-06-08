package net.seocraft.api.shared.online;

import com.google.inject.Inject;
import net.seocraft.api.shared.redis.RedisClient;
import redis.clients.jedis.Jedis;

public class OnlinePlayersImpl implements OnlinePlayersApi {

    @Inject private RedisClient client;

    private static final String PREFIX = "users.online";

    @Override
    public boolean isPlayerOnline(String id) {
        try (Jedis jedis = client.getPool().getResource()) {
            return jedis.sismember(PREFIX, id);
        }
    }

    @Override
    public void setPlayerOnlineStatus(String id, boolean onlineStatus) {
        try (Jedis jedis = client.getPool().getResource()) {
            boolean idIsMember = jedis.sismember(PREFIX, id);

            if (onlineStatus && !idIsMember) {
                jedis.sadd(PREFIX, id);

                return;
            }

            if (!onlineStatus && idIsMember) {
                jedis.srem(PREFIX, id);

                return;
            }

        }
    }
}
