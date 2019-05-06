package net.seocraft.api.bukkit.whisper;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.onlineplayers.OnlinePlayersApi;
import net.seocraft.api.shared.redis.Channel;
import net.seocraft.api.shared.redis.Messager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WhisperManagerImpl implements WhisperManager {

    private ListeningExecutorService executorService;
    private Channel<Whisper> whisperChannel;

    @Inject
    private OnlinePlayersApi onlinePlayersApi;

    @Inject
    WhisperManagerImpl(ListeningExecutorService executorService, Messager messager) {
        this.executorService = executorService;
        whisperChannel = messager.getChannel("whisper", Whisper.class);
    }

    @Override
    public ListenableFuture<WhisperResponse> sendMessage(User from, User to, String content) {
        final UUID fromUserId = UUID.fromString(from.id());
        final UUID toUserId = UUID.fromString(to.id());

        if (Bukkit.getPlayer(toUserId) != null) {
            Player playerFrom = Bukkit.getPlayer(fromUserId);
            Player playerTo = Bukkit.getPlayer(toUserId);

            // Someone set this to some sort of format
            playerFrom.sendMessage(content);
            playerTo.sendMessage(content);

            return Futures.immediateFuture(WhisperResponse.getSucessResponse(new WhisperImpl(from, to, content)));
        }

        return executorService.submit(() -> {
            Whisper whisper = new WhisperImpl(from, to, content);

            if (!onlinePlayersApi.isPlayerOnline(toUserId)) {
                return new WhisperResponse(null, WhisperResponse.Response.PLAYER_OFFLINE, whisper);
            }

            try {
                Player playerFrom = Bukkit.getPlayer(UUID.fromString(from.id()));

                // Set some sort of format
                playerFrom.sendMessage(content);

                whisperChannel.sendMessage(whisper);

                return new WhisperResponse(null, WhisperResponse.Response.SUCCESS, whisper);
            } catch (Exception ex) {
                return new WhisperResponse(null, WhisperResponse.Response.ERROR, whisper);
            }

        });
    }
}
