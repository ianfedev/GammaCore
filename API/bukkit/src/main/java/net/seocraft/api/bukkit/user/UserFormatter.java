package net.seocraft.api.bukkit.user;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface UserFormatter {
    @NotNull String getUserFormat(@NotNull User user, @NotNull String realm);

    @NotNull String getUserColor(@NotNull User user, @NotNull String realm);
}