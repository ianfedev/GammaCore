package net.seocraft.api.bukkit.user;

import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface UserLoginManagement {

    void loginUser(@NotNull Player player, @NotNull String password) throws IOException;

    void registerUser(@NotNull Player player, @NotNull String password) throws IOException;

    void checkUserJoinAttempts(@NotNull Player player, @NotNull User user);

}
