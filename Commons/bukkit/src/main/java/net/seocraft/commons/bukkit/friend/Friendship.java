package net.seocraft.commons.bukkit.friend;

import org.jetbrains.annotations.NotNull;

public interface Friendship {

    @NotNull String getSender();

    @NotNull String getReceiver();

    @NotNull FriendshipAction getAction();

    boolean isAlerted();

    void setAlerted(boolean alerted);

}
