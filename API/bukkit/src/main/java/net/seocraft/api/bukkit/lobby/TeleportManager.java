package net.seocraft.api.bukkit.lobby;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TeleportManager {

    void spawnTeleport(@NotNull Player player, @Nullable OfflinePlayer offlineTarget, boolean silent);

    void playerTeleport(@NotNull Player sender, @Nullable Player target, boolean silent);

    void playerTeleportOwn(@NotNull Player sender, @Nullable Player target, boolean silent);

    void playerTeleportAll(@NotNull Player player, boolean silent);

}
