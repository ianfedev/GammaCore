package net.seocraft.commons.core.redis;

import lombok.AllArgsConstructor;
import net.seocraft.api.core.redis.messager.ChannelListener;
import net.seocraft.commons.core.redis.messager.ObjectWrapper;
import org.redisson.api.listener.MessageListener;

@AllArgsConstructor
class RedissonChannelWrapper<T> implements MessageListener<ObjectWrapper<T>> {

    private String channelName;
    private String id;
    private ChannelListener<T> messageListener;

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