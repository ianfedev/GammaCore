package net.seocraft.api.core.online;

public interface OnlineStatusManager {

    boolean isPlayerOnline(String id);

    void setPlayerOnlineStatus(String id, boolean onlineStatus);

}
