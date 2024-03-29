package net.seocraft.commons.bukkit.whisper;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.whisper.Whisper;
import net.seocraft.api.bukkit.whisper.WhisperManager;
import net.seocraft.api.bukkit.whisper.WhisperResponse;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CraftWhisperManager implements WhisperManager {

    @Inject private TranslatableField translator;
    @Inject private OnlineStatusManager onlineStatusManager;
    private ListeningExecutorService executorService;
    private Channel<Whisper> whisperChannel;

    @Inject
    CraftWhisperManager(ListeningExecutorService executorService, Messager messager, WhisperListener whisperListener) {
        this.executorService = executorService;
        whisperChannel = messager.getChannel("whisper", Whisper.class);
        whisperChannel.registerListener(whisperListener);
    }

    @Override
    public @NotNull ListenableFuture<WhisperResponse> sendMessage(User from, User to, String content) {
        if (Bukkit.getPlayer(to.getUsername()) != null) {

            Player playerFrom = Bukkit.getPlayer(from.getUsername());
            Player playerTo = Bukkit.getPlayer(to.getUsername());

            playerFrom.sendMessage(
                    ChatColor.AQUA + this.translator.getField(from.getLanguage(), "commons_message_to") +
                            ChatColor.GRAY + playerTo.getName() + ": " + content
            );
            playerTo.sendMessage(
                    ChatColor.AQUA + this.translator.getField(from.getLanguage(), "commons_message_from") +
                            ChatColor.GRAY + playerFrom.getName() + ": " + content
            );

            return Futures.immediateFuture(WhisperResponse.getSucessResponse(new WhisperMessage(from, to, content)));
        }

        return executorService.submit(() -> {


            Whisper whisper = new WhisperMessage(from, to, content);

            if (!this.onlineStatusManager.isPlayerOnline(to.getId())) {
                return new WhisperResponse(null, WhisperResponse.Response.PLAYER_OFFLINE, whisper);
            }

            try {
                Player playerFrom = Bukkit.getPlayer(from.getUsername());
                playerFrom.sendMessage(
                        ChatColor.AQUA + this.translator.getField(from.getLanguage(), "commons_message_to") +
                                ChatColor.GRAY + to.getDisplay() + ": " + content
                );
                whisperChannel.sendMessage(whisper);

                return new WhisperResponse(null, WhisperResponse.Response.SUCCESS, whisper);
            } catch (Exception ex) {
                return new WhisperResponse(ex, WhisperResponse.Response.ERROR, whisper);
            }

        });
    }
}
