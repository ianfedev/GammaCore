package net.seocraft.api.shared.onlineplayers;

import com.google.inject.Inject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class OnlinePlayersImpl implements OnlinePlayersApi {

    @Inject
    private JedisPool jedisPool;

    private static final String PREFIX = "users.online";

    @Override
    public boolean isPlayerOnline(UUID id) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(PREFIX, id.toString());
        }
    }

    @Override
    public void setPlayerOnlineStatus(UUID id, boolean onlineStatus) {
        try (Jedis jedis = jedisPool.getResource()) {
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
