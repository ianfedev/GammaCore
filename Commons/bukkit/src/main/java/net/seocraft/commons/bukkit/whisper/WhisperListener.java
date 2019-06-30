package net.seocraft.commons.bukkit.whisper;

import com.google.inject.Inject;
import net.seocraft.api.shared.redis.ChannelListener;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WhisperListener implements ChannelListener<Whisper> {

    @Inject private TranslatableField translator;

    @Override
    public void receiveMessage(Whisper object) {
        if (object == null) {
            return;
        }

        final String toUserId = object.to().getUsername();

        Player playerTo = Bukkit.getPlayer(toUserId);

        if(playerTo == null){
            return;
        }

        playerTo.sendMessage(
                ChatColor.AQUA + this.translator.getField(object.to().getLanguage(), "commons_message_from") +
                        ChatColor.GRAY + object.from().getUsername() + ": " + object.content()
        );
    }
}
