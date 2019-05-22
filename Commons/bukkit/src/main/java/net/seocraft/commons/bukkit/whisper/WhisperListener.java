package net.seocraft.commons.bukkit.whisper;

import com.google.inject.Inject;
import net.seocraft.api.shared.redis.ChannelListener;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WhisperListener implements ChannelListener<Whisper> {

    @Inject
    private TranslatableField translator;

    @Override
    public void receiveMessage(Whisper object) {
        if (object == null) {
            return;
        }

        final UUID fromUserId = UUID.fromString(object.from().id());
        final UUID toUserId = UUID.fromString(object.to().id());

        Player playerTo = Bukkit.getPlayer(toUserId);

        playerTo.sendMessage(
                ChatColor.AQUA + this.translator.getField(object.to().getLanguage(), "commons_message_to") +
                        ChatColor.GRAY + object.from().getUsername() + ": " + object.content()
        );
    }
}
