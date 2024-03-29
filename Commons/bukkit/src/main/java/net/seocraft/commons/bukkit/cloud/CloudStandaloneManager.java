package net.seocraft.commons.bukkit.cloud;

import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.lobby.LobbyIcon;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CloudStandaloneManager implements CloudManager {
    @Override
    public void sendPlayerToServer(@NotNull Player player, @NotNull String server) {
        ChatAlertLibrary.errorChatAlert(player, "The server is not in Cloud environment, rejecting teleport request.");
    }

    @Override
    public void sendPlayerToGroup(@NotNull Player player, @NotNull String group) {
        ChatAlertLibrary.errorChatAlert(player, "The server is not in Cloud environment, rejecting teleport request.");
    }

    @Override
    public @NotNull Set<LobbyIcon> getGroupLobbies(@NotNull String group) {
        Set<LobbyIcon> deniedGroup = new HashSet<>();
        deniedGroup.add(
                new GammaLobbyIcon("Not in cloud environment", 0, 0, 0)
        );
        return deniedGroup;
    }

    @Override
    public int getGroupOnlinePlayers(@NotNull String id) {
        return 0;
    }

    @Override
    public int getGamemodeOnlinePlayers(@NotNull Gamemode gamemode) {
        return 0;
    }

    @Override
    public int getOnlinePlayers() {
        return 0;
    }

    @Override
    public @NotNull UUID createCloudService(@NotNull String taskName) {
        return UUID.randomUUID();
    }

    @Override
    public boolean isConnected(@NotNull UUID service) {
        return false;
    }

    @Override
    public @NotNull String getSlug(@NotNull UUID service) {
        return "NCI";
    }
}
