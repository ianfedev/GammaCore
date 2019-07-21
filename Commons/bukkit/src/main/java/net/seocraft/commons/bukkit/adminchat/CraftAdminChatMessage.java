package net.seocraft.commons.bukkit.adminchat;


import net.seocraft.api.bukkit.adminchat.AdminChatMessage;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public class CraftAdminChatMessage implements AdminChatMessage {

    @NotNull private User user;
    @NotNull private String content;
    private boolean important;

    CraftAdminChatMessage(@NotNull User user, @NotNull String content, boolean important) {
        this.user = user;
        this.content = content;
        this.important = important;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @NotNull String getContent() {
        return content;
    }

    public boolean isImportant() {
        return important;
    }
}
