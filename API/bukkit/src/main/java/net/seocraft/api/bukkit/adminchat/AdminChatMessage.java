package net.seocraft.api.bukkit.adminchat;


import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface AdminChatMessage {

    @NotNull User getUser();

    @NotNull String getContent();

    boolean isImportant();
}