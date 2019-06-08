package net.seocraft.commons.bukkit.adminchat;

import net.seocraft.api.shared.model.User;

public class AdminChatMessageImpl implements AdminChatMessage {
    private User user;
    private String content;
    private boolean important;

    AdminChatMessageImpl(User user, String content, boolean important) {
        this.user = user;
        this.content = content;
        this.important = important;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public boolean isImportant() {
        return important;
    }
}
