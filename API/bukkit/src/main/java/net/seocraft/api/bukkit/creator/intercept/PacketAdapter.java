package net.seocraft.api.bukkit.creator.intercept;

import io.netty.channel.Channel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class PacketAdapter implements PacketListener {

    @NotNull private String packetName;

    protected PacketAdapter(@NotNull String packetName) {
        this.packetName = packetName;
    }
    @Override
    public Object onPacketPlayIn(@NotNull Player receiver, @NotNull Channel channel, @NotNull Object packet) {
        if (packet.getClass().getSimpleName().equals(packetName)) return onPacketReceiving(receiver, channel, packet);
        return packet;
    }

    @Override
    public Object onPacketPlayOut(@NotNull Player sender, @NotNull Channel channel, @NotNull Object packet) {
        if (packet.getClass().getSimpleName().equals(packetName)) return onPacketReceiving(sender, channel, packet);
        return packet;
    }

    public abstract Object onPacketReceiving(@NotNull Player target, @NotNull Channel channel, @NotNull Object packet);
}