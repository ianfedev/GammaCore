package net.seocraft.commons.bukkit.adminchat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.redis.Channel;
import net.seocraft.api.shared.redis.IRedisClient;
import net.seocraft.api.shared.redis.Messager;

import java.util.Map;
import java.util.UUID;

public class AdminChatManagerImpl implements AdminChatManager {

    @Inject
    private IRedisClient redisClient;

    @Inject
    private ListeningExecutorService executorService;

    private Channel<AdminChatMessageImpl> adminChatChannel;

    @Inject
    AdminChatManagerImpl(Messager messager) {
        adminChatChannel = messager.getChannel("adminChat", AdminChatMessageImpl.class);

        adminChatChannel.registerListener(message -> {

        });
    }


    @Override
    public ListenableFuture<AdminChatMessage> sendMessage(User user, String content, boolean important) {
        return executorService.submit(() -> {
            AdminChatMessageImpl chatMessage = new AdminChatMessageImpl(user, content, important);

            adminChatChannel.sendMessage(chatMessage);

            CallbackWrapper.addCallback(getPlayersInAdminChat(), map -> {
            });

            return chatMessage;
        });
    }

    @Override
    public ListenableFuture<Map<UUID, Boolean>> getPlayersInAdminChat() {

        return null;
    }

    @Override
    public ListenableFuture<Void> removePlayerFromAdminChat(UUID uniqueId) {
        return null;
    }

    @Override
    public ListenableFuture<Void> addPlayerToAdminChat(UUID uniqueId, boolean active) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> hasPlayerAdminChatActive(UUID uniqueId) {
        return null;
    }

}
