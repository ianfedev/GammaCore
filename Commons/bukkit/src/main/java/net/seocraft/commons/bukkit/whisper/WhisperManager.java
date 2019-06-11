package net.seocraft.commons.bukkit.whisper;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.user.model.User;

public interface WhisperManager {
    ListenableFuture<WhisperResponse> sendMessage(User from, User to, String content);
}
