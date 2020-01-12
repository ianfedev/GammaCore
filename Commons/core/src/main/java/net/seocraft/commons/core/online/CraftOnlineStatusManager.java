package net.seocraft.commons.core.online;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CraftOnlineStatusManager implements OnlineStatusManager {

    @Inject private RedisClient client;
    @Inject private ObjectMapper mapper;

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

    @Override
    public @NotNull Set<User> getOnlinePlayers() {
        return this.client.getKeys("user:*")
                .stream().map((key) -> {
                    User user;
                    try {
                        user = this.mapper.readValue(
                                this.client.getString(key),
                                User.class
                        );
                        if (isPlayerOnline(user.getId())) return user;
                        return null;
                    } catch (IOException ignore) {}
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
