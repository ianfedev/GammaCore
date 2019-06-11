package net.seocraft.commons.bukkit.adminchat;


import net.seocraft.api.shared.user.model.User;

public interface AdminChatMessage {

    User getUser();

    String getContent();

    boolean isImportant();
}