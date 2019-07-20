package net.seocraft.commons.bukkit.old.adminchat;


import net.seocraft.api.core.user.User;

public interface AdminChatMessage {

    User getUser();

    String getContent();

    boolean isImportant();
}