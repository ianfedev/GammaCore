package net.seocraft.commons.bukkit.old.whisper;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.core.user.User;

public interface WhisperManager {
    ListenableFuture<WhisperResponse> sendMessage(User from, User to, String content);
}
