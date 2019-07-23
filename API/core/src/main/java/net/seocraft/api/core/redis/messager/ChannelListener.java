package net.seocraft.api.core.redis.messager;

public interface ChannelListener<O> {
    void receiveMessage(O object);
}
