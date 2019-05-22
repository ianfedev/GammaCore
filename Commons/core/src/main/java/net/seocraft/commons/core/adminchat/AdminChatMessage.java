package net.seocraft.commons.core.adminchat;

import net.seocraft.api.shared.models.User;
import org.jetbrains.annotations.NotNull;

public interface AdminChatMessage {
    @NotNull
    User getUser();

    @NotNull
    String getContent();

    boolean isImportant();
}