package net.seocraft.api.bukkit.whisper;

import net.seocraft.api.shared.redis.ChannelListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WhisperListener implements ChannelListener<Whisper> {
    @Override
    public void receiveMessage(Whisper object) {
        if (object == null) {
            return; // Invalid request, how this even was send?
        }

        final UUID fromUserId = UUID.fromString(object.from().id());
        final UUID toUserId = UUID.fromString(object.to().id());

        Player playerFrom = Bukkit.getPlayer(fromUserId);
        Player playerTo = Bukkit.getPlayer(toUserId);

        playerTo.sendMessage(object.content());
    }
}
