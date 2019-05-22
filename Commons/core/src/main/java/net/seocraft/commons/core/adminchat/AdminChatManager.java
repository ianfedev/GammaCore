package net.seocraft.commons.core.adminchat;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.models.User;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface AdminChatManager {

    ListenableFuture<AdminChatMessage> sendMessage(@NotNull User user, @NotNull String content, boolean important);

    ListenableFuture<Map<UUID, Boolean>> getPlayersInAdminChat();

    ListenableFuture<Void> removePlayerFromAdminChat(@NotNull UUID uniqueId);

    ListenableFuture<Void> addPlayerToAdminChat(@NotNull UUID uniqueId, boolean active);

    ListenableFuture<Boolean> hasPlayerAdminChatActive(@NotNull UUID uniqueId);
}
