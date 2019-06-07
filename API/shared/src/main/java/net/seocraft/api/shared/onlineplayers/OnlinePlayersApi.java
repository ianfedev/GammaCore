package net.seocraft.api.shared.onlineplayers;

public interface OnlinePlayersApi {
    boolean isPlayerOnline(String id);

    void setPlayerOnlineStatus(String id, boolean onlineStatus);
}
