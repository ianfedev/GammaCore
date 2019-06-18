package net.seocraft.commons.bukkit.friend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FriendshipImpl implements Friendship {

    private String sender;
    private String receiver;
    @Nullable private String issuer;
    private FriendshipAction action;
    private boolean alerted;

    public FriendshipImpl(String sender, String receiver, FriendshipAction action, boolean alerted, @Nullable String issuer) {
        this.sender = sender;
        this.receiver = receiver;
        this.action = action;
        this.alerted = alerted;
        this.issuer = issuer;
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
    public @Nullable String getIssuer() {
        return this.issuer;
    }

    @Override
    public @NotNull FriendshipAction getAction() {
        return this.action;
    }

    @Override
    public boolean isAlerted() {
        return this.alerted;
    }

    @Override
    public void setAlerted(boolean alerted) {
        this.alerted = alerted;
    }
}
