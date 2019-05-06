package net.seocraft.api.shared.onlineplayers;

import java.util.UUID;

public interface OnlinePlayersApi {
    boolean isPlayerOnline(UUID id);

    void setPlayerOnlineStatus(UUID id, boolean onlineStatus);
}
