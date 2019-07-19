package net.seocraft.api.shared.redis;

import lombok.AllArgsConstructor;
import org.redisson.api.listener.MessageListener;


@AllArgsConstructor
public class RedissonWrapper<O> implements MessageListener<RedisWrapper<O>> {

    private ChannelListener<O> channelListener;
    private String serverChannelId;

    @Override
    public void onMessage(CharSequence charSequence, RedisWrapper<O> wrapper) {
        if(wrapper.getId().equals(serverChannelId)){
            return;
        }

        channelListener.receiveMessage(wrapper.getObject());
    }
}
