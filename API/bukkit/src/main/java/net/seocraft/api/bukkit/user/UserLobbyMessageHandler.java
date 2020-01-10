package net.seocraft.api.bukkit.user;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface UserLobbyMessageHandler {

    void alertUserJoinMessage(@NotNull User user);

}
