package net.seocraft.commons.bukkit.adminchat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.adminchat.AdminChatManager;
import net.seocraft.api.bukkit.adminchat.AdminChatMessage;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class CraftAdminChatManager implements AdminChatManager {

    @Inject private RedisClient redisClient;

    @Inject
    private ListeningExecutorService executorService;

    private Channel<CraftAdminChatMessage> adminChatChannel;

    @Inject
    CraftAdminChatManager(Messager messager) {
        adminChatChannel = messager.getChannel("adminChat", CraftAdminChatMessage.class);

        adminChatChannel.registerListener(message -> {

        });
    }


    @Override
    public @NotNull ListenableFuture<AdminChatMessage> sendMessage(User user, String content, boolean important) {
        return executorService.submit(() -> {
            CraftAdminChatMessage chatMessage = new CraftAdminChatMessage(user, content, important);

            adminChatChannel.sendMessage(chatMessage);

            /*CallbackWrapper.addCallback(getPlayersInAdminChat(), map -> {
            });*/

            return chatMessage;
        });
    }


}
