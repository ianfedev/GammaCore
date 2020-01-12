package net.seocraft.commons.bukkit.channel.admin;

import net.seocraft.api.bukkit.channel.admin.ACMessage;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

public class GammaACMessage implements ACMessage {

    @NotNull private String message;
    @NotNull private User sender;
    @NotNull private Set<User> mentionUsers;
    private boolean important;

    @ConstructorProperties({
            "message",
            "sender",
            "mentionUsers",
            "important"
    })
    public GammaACMessage(@NotNull String message, @NotNull User sender, @NotNull Set<User> mentionUsers, boolean important) {
        this.message = message;
        this.sender = sender;
        this.mentionUsers = mentionUsers;
        this.important = important;
    }

    @Override
    public @NotNull String getMessage() {
        return this.message;
    }

    @Override
    public @NotNull User getSender() {
        return this.sender;
    }

    @Override
    public @NotNull Set<User> getMentionUsers() {
        return this.mentionUsers;
    }

    @Override
    public boolean isImportant() {
        return this.important;
    }
}
