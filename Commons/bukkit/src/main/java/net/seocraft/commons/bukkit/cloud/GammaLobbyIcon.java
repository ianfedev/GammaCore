package net.seocraft.commons.bukkit.cloud;

import net.seocraft.api.bukkit.lobby.LobbyIcon;
import org.jetbrains.annotations.NotNull;

public class GammaLobbyIcon implements LobbyIcon {

    @NotNull private String name;
    private int onlinePlayers;
    private int number;
    private int maxPlayers;

    GammaLobbyIcon(@NotNull String name, int onlinePlayers, int number, int maxPlayers) {
        this.name = name;
        this.onlinePlayers = onlinePlayers;
        this.number = number;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }

    @Override
    public int getNumber() {
        return this.number;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public boolean isFull() {
        return this.number == this.maxPlayers;
    }
}
