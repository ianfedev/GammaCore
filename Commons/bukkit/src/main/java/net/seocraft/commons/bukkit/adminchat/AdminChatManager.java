package net.seocraft.commons.bukkit.adminchat;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.user.model.User;

import java.util.Map;
import java.util.UUID;

public interface AdminChatManager {

    ListenableFuture<AdminChatMessage> sendMessage(User user, String content, boolean important);

    ListenableFuture<Map<UUID, Boolean>> getPlayersInAdminChat();

    ListenableFuture<Void> removePlayerFromAdminChat(UUID uniqueId);

    ListenableFuture<Void> addPlayerToAdminChat(UUID uniqueId, boolean active);

    ListenableFuture<Boolean> hasPlayerAdminChatActive(UUID uniqueId);
}
