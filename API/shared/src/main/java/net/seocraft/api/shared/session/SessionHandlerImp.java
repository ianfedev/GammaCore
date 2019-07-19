package net.seocraft.api.shared.session;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.seocraft.api.shared.redis.RedisClientImpl;
import net.seocraft.api.shared.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SessionHandlerImp implements SessionHandler {

    @Inject private RedisClientImpl client;
    @Inject private Gson gson;

    @Override
    public void createGameSession(@NotNull User user, String address, String version) {
        if (!this.client.existsKey("session:" + user.getUsername().toLowerCase())) {
            this.client.setString("session:" + user.getUsername().toLowerCase(),
                    this.gson.toJson(new GameSessionImp(user.id(), address, version))
            );
        }
    }

    @Override
    public @Nullable GameSession getCachedSession(@NotNull String username) {
        return this.gson.fromJson(this.client.getString("session:" + username.toLowerCase()), GameSessionImp.class);
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
