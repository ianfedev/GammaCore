package net.seocraft.commons.bukkit.channel.admin;

import net.seocraft.api.bukkit.channel.admin.ACBroadcaster;
import net.seocraft.api.bukkit.channel.admin.ACMessage;
import net.seocraft.api.core.redis.messager.ChannelListener;
import org.jetbrains.annotations.NotNull;

public class ACMessageListener implements ChannelListener<ACMessage> {

    @NotNull private ACBroadcaster broadcaster;

    ACMessageListener(@NotNull ACBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void receiveMessage(ACMessage object) {
        this.broadcaster.deliveryMessage(object);
    }

}
