package net.seocraft.api.shared.redis;

public interface ChannelListener<O> {
    void receiveMessage(O object);
}
