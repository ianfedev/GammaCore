package net.seocraft.api.shared.onlineplayers;

import com.google.inject.Inject;
import net.seocraft.api.shared.redis.RedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class OnlinePlayersImpl implements OnlinePlayersApi {

    @Inject private RedisClient client;

    private static final String PREFIX = "users.online";

    @Override
    public boolean isPlayerOnline(UUID id) {
        try (Jedis jedis = client.getPool().getResource()) {
            return jedis.sismember(PREFIX, id.toString());
        }
    }

    @Override
    public void setPlayerOnlineStatus(UUID id, boolean onlineStatus) {
        try (Jedis jedis = client.getPool().getResource()) {
            boolean idIsMember = jedis.sismember(PREFIX, id.toString());

            if (onlineStatus && !idIsMember) {
                jedis.sadd(PREFIX, id.toString());

                return;
            }

            if (!onlineStatus && idIsMember) {
                jedis.srem(PREFIX, id.toString());

                return;
            }

        }
    }
}
