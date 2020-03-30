package net.seocraft.api.core.user.partial;

import org.jetbrains.annotations.NotNull;

public interface SessionInfo {

    long getLastSeen();

    void setLastSeen(long lastSeen);

    boolean isOnline();

    @NotNull String getLastGame();

    void setLastGame(@NotNull String lastGame);

    @NotNull String getLastLobby();

    void setLastLobby(@NotNull String lastLobby);

    boolean isPremium();

    void setPremium(boolean premium);

}
