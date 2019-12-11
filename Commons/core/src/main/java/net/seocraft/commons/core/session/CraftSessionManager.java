package net.seocraft.commons.core.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class CraftSessionManager implements GameSessionManager {

    @Inject private RedisClient client;
    @Inject private ObjectMapper mapper;

    @Override
    public void createGameSession(@NotNull User user, String address, String version) throws JsonProcessingException {
        if (!this.client.existsKey("session:" + user.getUsername().toLowerCase())) {
            this.client.setString("session:" + user.getUsername().toLowerCase(),
                    this.mapper.writeValueAsString(new CraftSession(user.getId(), address, version))
            );
        }
    }

    @Override
    public @Nullable GameSession getCachedSession(@NotNull String username) throws IOException {
        if (sessionExists(username)) {
            return this.mapper.readValue(
                    this.client.getString("session:" + username.toLowerCase()),
                    GameSession.class
            );
        }
        return null;
    }

    @Override
    public boolean sessionExists(@NotNull String username) {
        return this.client.existsKey("session:" + username.toLowerCase());
    }

    @Override
    public void removeGameSession(@NotNull String username) {
        if (this.client.existsKey("session:" + username.toLowerCase())) {
            this.client.deleteString("session:" + username.toLowerCase());
        }
    }
}
