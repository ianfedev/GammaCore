package net.seocraft.api.bukkit.cloud;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CloudLobbySwitcher {

    void sendPlayerToServer(@NotNull Player player, @NotNull String server);

    void sendPlayerToGroup(@NotNull Player player, @NotNull String group);
}
