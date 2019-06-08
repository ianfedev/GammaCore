package net.seocraft.api.shared.online;

public interface OnlinePlayersApi {
    boolean isPlayerOnline(String id);

    void setPlayerOnlineStatus(String id, boolean onlineStatus);
}
