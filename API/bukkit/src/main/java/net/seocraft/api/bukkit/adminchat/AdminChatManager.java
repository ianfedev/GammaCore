package net.seocraft.api.bukkit.adminchat;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface AdminChatManager {
    @NotNull ListenableFuture<AdminChatMessage> sendMessage(User user, String content, boolean important);
}
