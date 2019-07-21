package net.seocraft.api.bukkit.whisper;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface WhisperManager {
    @NotNull ListenableFuture<WhisperResponse> sendMessage(User from, User to, String content);
}
