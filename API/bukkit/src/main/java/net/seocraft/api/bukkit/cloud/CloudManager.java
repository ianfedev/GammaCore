package net.seocraft.api.bukkit.cloud;

import net.seocraft.api.bukkit.lobby.LobbyIcon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface CloudManager {

    void sendPlayerToServer(@NotNull Player player, @NotNull String server);

    void sendPlayerToGroup(@NotNull Player player, @NotNull String group);

    Set<LobbyIcon> getGroupLobbies(@NotNull String group);
}
