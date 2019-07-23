package net.seocraft.api.bukkit.adminchat;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface AdminChatManager {

    @NotNull ListenableFuture<AdminChatMessage> sendMessage(User user, String content, boolean important);

    @NotNull ListenableFuture<Map<UUID, Boolean>> getPlayersInAdminChat();

    @NotNull ListenableFuture<Void> removePlayerFromAdminChat(UUID uniqueId);

    @NotNull ListenableFuture<Void> addPlayerToAdminChat(UUID uniqueId, boolean active);

    @NotNull ListenableFuture<Boolean> hasPlayerAdminChatActive(UUID uniqueId);
}
