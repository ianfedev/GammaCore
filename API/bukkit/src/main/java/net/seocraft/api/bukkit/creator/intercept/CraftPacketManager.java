package net.seocraft.api.bukkit.creator.intercept;

import com.google.inject.Inject;
import lombok.Getter;
import net.seocraft.lib.netty.channel.Channel;
import net.seocraft.lib.netty.channel.ChannelDuplexHandler;
import net.seocraft.lib.netty.channel.ChannelHandlerContext;
import net.seocraft.lib.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CraftPacketManager implements PacketManager {

    private List<PacketListener> packetListeners;

    private Class<?> PLAYER_CONNECTION_CLASS = Reflection.getClass("{nms}.PlayerConnection");
    private Class<?> NETWORK_MANAGER_CLASS = Reflection.getClass("{nms}.NetworkManager");

    private Field PLAYER_CONNECTION_FIELD = Reflection.getField(Reflection.getClass("{nms}.EntityPlayer"), "playerConnection");
    private Field PLAYER_NETWORK_MANAGER_FIELD = Reflection.getField(PLAYER_CONNECTION_CLASS, "networkManager");
    private Field CHANNEL_FIELD = Reflection.getField(NETWORK_MANAGER_CLASS, "channel");

    @Inject
    CraftPacketManager(ArrayList<PacketListener> packetListeners) {
        this.packetListeners = packetListeners;
    }

    @Override
    public void addPacketListener(@NotNull PacketListener packetListener) {
        this.packetListeners.add(packetListener);
    }

    @Override
    public void injectPlayer(@NotNull Player player) {
        ChannelDuplexHandler duplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                for (PacketListener packetListener : packetListeners) {
                    System.out.println("Calling packet listener with hashcode " + packetListener.hashCode());
                    packet = packetListener.onPacketPlayIn(player, channelHandlerContext.channel(), packet);
                }
                if (packet != null) {
                    super.channelRead(channelHandlerContext, packet);
                }
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                for (PacketListener packetListener : packetListeners) {
                    packet = packetListener.onPacketPlayOut(player, channelHandlerContext.channel(), packet);
                }
                if (packet != null) {
                    super.write(channelHandlerContext, packet, channelPromise);
                }
            }
        };
        Channel channel = getChannel(this.getNetworkManager(Reflection.getNmsPlayer(player)));
        channel.pipeline().addBefore("packet_handler", player.getName(), duplexHandler);
    }

    @Override
    public void uninjectPlayer(@NotNull Player player) {
        Channel ch = getChannel(this.getNetworkManager(Reflection.getNmsPlayer(player)));
        if(ch.pipeline().get("PacketInjector") != null) {
            ch.pipeline().remove("PacketInjector");
        }
    }

    private Object getNetworkManager(@NotNull Object entity) {
        return Reflection.getFieldValue(PLAYER_NETWORK_MANAGER_FIELD, (Object) Reflection.getFieldValue(PLAYER_CONNECTION_FIELD, entity));
    }

    private Channel getChannel(@NotNull Object networkManager) {
        return Reflection.getFieldValue(CHANNEL_FIELD, networkManager);
    }
}