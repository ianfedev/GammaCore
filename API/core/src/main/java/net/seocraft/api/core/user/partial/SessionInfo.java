package net.seocraft.api.core.user.partial;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public interface SessionInfo {

    @NotNull Date getLastSeen();

    void setLastSeen(@NotNull Date lastSeen);

    boolean isOnline();

    @NotNull String getLastGame();

    void setLastGame(@NotNull String lastGame);

    @NotNull String getLastLobby();

    void setLastLobby(@NotNull String lastLobby);

    boolean isPremium();

    void setPremium(boolean premium);

}
