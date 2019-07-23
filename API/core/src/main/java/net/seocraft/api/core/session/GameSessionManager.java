package net.seocraft.api.core.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface GameSessionManager {

    void createGameSession(@NotNull User user, String address, String version) throws JsonProcessingException;

    @Nullable GameSession getCachedSession(@NotNull String username) throws IOException;

    boolean sessionExists(@NotNull String username);

    void removeGameSession(@NotNull String username);
}
