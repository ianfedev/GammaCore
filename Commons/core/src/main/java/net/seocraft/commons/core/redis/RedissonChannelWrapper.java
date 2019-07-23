package net.seocraft.commons.core.redis;

import net.seocraft.api.core.redis.messager.ChannelListener;
import net.seocraft.commons.core.redis.messager.ObjectWrapper;
import org.redisson.api.listener.MessageListener;

public class RedissonChannelWrapper<T> implements MessageListener<ObjectWrapper<T>> {

    private String channelName;
    private String id;
    private ChannelListener<T> messageListener;

    public RedissonChannelWrapper(String channelName, String id, ChannelListener<T> messageListener) {
        this.channelName = channelName;
        this.id = id;
        this.messageListener = messageListener;
    }

    @Override
    public void onMessage(CharSequence channel, ObjectWrapper<T> object) {
        if (!channel.equals(channelName)) {
            return;
        }

        if (object.getServerSenderId().equals(id)) {
            return;
        }

        messageListener.receiveMessage(object.getObject());
    }
}