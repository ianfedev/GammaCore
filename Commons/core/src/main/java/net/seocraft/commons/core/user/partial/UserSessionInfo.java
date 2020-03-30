package net.seocraft.commons.core.user.partial;

import net.seocraft.api.core.user.partial.SessionInfo;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class UserSessionInfo implements SessionInfo {

    private long lastSeen;
    private boolean online;
    @NotNull private String lastGame;
    @NotNull private String lastLobby;
    private boolean premium;

    @ConstructorProperties({
            "lastSeen",
            "online",
            "lastGame",
            "lastLobby",
            "premium"
    })
    public UserSessionInfo(long lastSeen, boolean online, @NotNull String lastGame, @NotNull String lastLobby, boolean premium) {
        this.lastSeen = lastSeen;
        this.online = online;
        this.lastGame = lastGame;
        this.lastLobby = lastLobby;
        this.premium = premium;
    }

    @Override
    public long getLastSeen() {
        return this.lastSeen;
    }

    @Override
    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public boolean isOnline() {
        return this.online;
    }

    @Override
    public @NotNull String getLastGame() {
        return this.lastGame;
    }

    @Override
    public void setLastGame(@NotNull String lastGame) {
        this.lastGame = lastGame;
    }

    @Override
    public @NotNull String getLastLobby() {
        return this.lastLobby;
    }

    @Override
    public void setLastLobby(@NotNull String lastLobby) {
        this.lastLobby = lastLobby;
    }

    @Override
    public boolean isPremium() {
        return this.premium;
    }

    @Override
    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
