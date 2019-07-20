package net.seocraft.commons.bukkit.old.adminchat;


import net.seocraft.api.core.user.User;

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
