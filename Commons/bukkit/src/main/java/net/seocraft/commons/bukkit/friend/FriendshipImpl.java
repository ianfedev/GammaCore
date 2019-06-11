package net.seocraft.commons.bukkit.friend;

import org.jetbrains.annotations.NotNull;

public class FriendshipImpl implements Friendship {

    private String sender;
    private String receiver;
    private FriendshipAction action;

    public FriendshipImpl(String sender, String receiver, FriendshipAction action) {
        this.sender = sender;
        this.receiver = receiver;
        this.action = action;
    }

    @Override
    public @NotNull String getSender() {
        return this.sender;
    }

    @Override
    public @NotNull String getReceiver() {
        return this.receiver;
    }

    @Override
    public @NotNull FriendshipAction getAction() {
        return this.action;
    }
}
