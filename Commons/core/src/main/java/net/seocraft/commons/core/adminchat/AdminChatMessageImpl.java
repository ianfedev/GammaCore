package net.seocraft.commons.core.adminchat;

import net.seocraft.api.shared.models.User;
import org.jetbrains.annotations.NotNull;

public class AdminChatMessageImpl implements AdminChatMessage {
    private @NotNull User user;
    private @NotNull String content;
    private boolean important;

    AdminChatMessageImpl(@NotNull User user, @NotNull String content, boolean important) {
        this.user = user;
        this.content = content;
        this.important = important;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    public boolean isImportant() {
        return important;
    }
}
