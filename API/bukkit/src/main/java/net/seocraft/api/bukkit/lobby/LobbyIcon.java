package net.seocraft.api.bukkit.lobby;

import org.jetbrains.annotations.NotNull;

public interface LobbyIcon {

    @NotNull String getName();

    int getOnlinePlayers();

    int getNumber();

    int getMaxPlayers();

    boolean isFull();

}
