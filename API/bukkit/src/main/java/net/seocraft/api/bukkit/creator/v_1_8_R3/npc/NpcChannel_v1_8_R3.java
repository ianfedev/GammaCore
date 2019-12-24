package net.seocraft.api.bukkit.creator.v_1_8_R3.npc;

import io.netty.channel.*;

import java.net.SocketAddress;

public class NpcChannel_v1_8_R3 extends AbstractChannel {

    private final ChannelConfig config = new DefaultChannelConfig(this);

    public NpcChannel_v1_8_R3() {
        this(null);
    }

    public NpcChannel_v1_8_R3(Channel parent) {
        super(parent);
    }

    @Override
    public ChannelConfig config() {
        this.config.setAutoRead(true);
        return this.config;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public ChannelMetadata metadata() {
        return null;
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return null;
    }

    @Override
    protected boolean isCompatible(EventLoop eventloop) {
        return false;
    }

    @Override
    protected SocketAddress localAddress0() {
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
    }

    @Override
    protected void doDisconnect() throws Exception {
    }

    @Override
    protected void doClose() throws Exception {
    }

    @Override
    protected void doBeginRead() throws Exception {
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
    }

}
