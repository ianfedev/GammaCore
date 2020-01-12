package net.seocraft.api.bukkit.channel.admin;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface ACLoginBroadcaster {

    void broadcastLogin(@NotNull User session, boolean important);

    void broadcastLogout(@NotNull User session, boolean important);
}
