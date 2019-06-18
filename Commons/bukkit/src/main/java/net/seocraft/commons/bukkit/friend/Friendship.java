package net.seocraft.commons.bukkit.friend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Friendship {

    @NotNull String getSender();

    @NotNull String getReceiver();

    @Nullable String getIssuer();

    @NotNull FriendshipAction getAction();

    boolean isAlerted();

    void setAlerted(boolean alerted);

}
