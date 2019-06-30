package net.seocraft.commons.bukkit.whisper;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.shared.redis.Channel;
import net.seocraft.api.shared.redis.Messager;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WhisperManagerImpl implements WhisperManager {

    @Inject private SessionHandler sessionHandler;
    @Inject private TranslatableField translator;
    private ListeningExecutorService executorService;
    private Channel<Whisper> whisperChannel;

    @Inject
    WhisperManagerImpl(ListeningExecutorService executorService, Messager messager,WhisperListener whisperListener) {
        this.executorService = executorService;
        whisperChannel = messager.getChannel("whisper", Whisper.class);
        whisperChannel.registerListener(whisperListener);
    }

    @Override
    public ListenableFuture<WhisperResponse> sendMessage(User from, User to, String content) {
        if (Bukkit.getPlayer(to.getUsername()) != null) {
            Player playerFrom = Bukkit.getPlayer(from.getUsername());
            Player playerTo = Bukkit.getPlayer(to.getUsername());

            playerFrom.sendMessage(
                    ChatColor.AQUA + this.translator.getField(from.getLanguage(), "commons_message_from") +
                            ChatColor.GRAY + playerFrom.getName() + ": " + content
            );
            playerTo.sendMessage(
                    ChatColor.AQUA + this.translator.getField(from.getLanguage(), "commons_message_to") +
                            ChatColor.GRAY + playerFrom.getName() + ": " + content
            );

            return Futures.immediateFuture(WhisperResponse.getSucessResponse(new WhisperImpl(from, to, content)));
        }

        return executorService.submit(() -> {
            Whisper whisper = new WhisperImpl(from, to, content);

            if (!sessionHandler.sessionExists(to.getUsername())) {
                return new WhisperResponse(null, WhisperResponse.Response.PLAYER_OFFLINE, whisper);
            }

            try {
                Player playerFrom = Bukkit.getPlayer(from.getUsername());

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
