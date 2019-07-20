package net.seocraft.api.core.session;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GameSessionManager {

    void createGameSession(@NotNull User user, String address, String version);

    @Nullable GameSession getCachedSession(@NotNull String username);

    boolean sessionExists(@NotNull String username);

    void removeGameSession(@NotNull String username);
}
