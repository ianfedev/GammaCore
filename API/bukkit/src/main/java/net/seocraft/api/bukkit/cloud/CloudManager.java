package net.seocraft.api.bukkit.cloud;

import net.seocraft.api.bukkit.lobby.LobbyIcon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public interface CloudManager {

    void sendPlayerToServer(@NotNull Player player, @NotNull String server);

    void sendPlayerToGroup(@NotNull Player player, @NotNull String group);

    @NotNull Set<LobbyIcon> getGroupLobbies(@NotNull String group);

    int getOnlinePlayers();

    @NotNull UUID createCloudService(@NotNull String taskName);

    boolean isConnected(@NotNull UUID service);

    @NotNull String getSlug(@NotNull UUID service);
}
