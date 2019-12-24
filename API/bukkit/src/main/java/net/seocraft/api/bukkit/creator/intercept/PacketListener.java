package net.seocraft.api.bukkit.creator.intercept;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PacketListener {

    default Object onPacketPlayOut(@NotNull Player sender, @NotNull Channel channel, @NotNull Object packet) {return packet;}

    default Object onPacketPlayIn(@NotNull Player receiver, @NotNull Channel channel, @NotNull Object packet) {return packet;}

}