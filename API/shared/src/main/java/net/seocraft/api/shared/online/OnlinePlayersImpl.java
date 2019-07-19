package net.seocraft.api.shared.online;

import com.google.inject.Inject;
import net.seocraft.api.shared.redis.RedisClientImpl;

public class OnlinePlayersImpl implements OnlinePlayersApi {

    @Inject
    private RedisClientImpl client;

    private static final String PREFIX = "users.online";

    @Override
    public boolean isPlayerOnline(String id) {
        return client.existsInSet(PREFIX, id);
    }

    @Override
    public void setPlayerOnlineStatus(String id, boolean onlineStatus) {
        boolean idIsMember = isPlayerOnline(id);

        if (onlineStatus && !idIsMember) {
            client.addToSet(PREFIX, id);
            return;
        }

        if (!onlineStatus && idIsMember) {
            client.removeFromSet(PREFIX, id);
        }
    }
}
