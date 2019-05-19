package net.seocraft.commons.core.adminchat;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.models.User;

import java.util.Map;
import java.util.UUID;

public interface AdminChatManager {

    ListenableFuture<AdminChatMessage> sendMessage(User user, String content, boolean important);

    ListenableFuture<Map<UUID, Boolean>> getPlayersInAdminChat();

    ListenableFuture<Void> removePlayerFromAdminChat(UUID uniqueId);

    ListenableFuture<Void> addPlayerToAdminChat(UUID uniqueId, boolean active);

    ListenableFuture<Boolean> hasPlayerAdminChatActive(UUID uniqueId);

    class AdminChatMessage {
        private User user;
        private String content;
        private boolean important;

        AdminChatMessage(User user, String content, boolean important) {
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
}
