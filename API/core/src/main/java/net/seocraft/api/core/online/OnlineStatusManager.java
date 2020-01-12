package net.seocraft.api.core.online;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface OnlineStatusManager {

    boolean isPlayerOnline(String id);

    void setPlayerOnlineStatus(String id, boolean onlineStatus);

    @NotNull Set<User> getOnlinePlayers();

}
