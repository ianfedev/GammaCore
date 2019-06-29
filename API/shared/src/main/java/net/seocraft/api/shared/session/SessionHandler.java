package net.seocraft.api.shared.session;

import net.seocraft.api.shared.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SessionHandler {

    void createGameSession(@NotNull User user, String address, String version);

    @Nullable GameSession getCachedSession(@NotNull String username);

    void removeGameSession(@NotNull String username);
}
