package net.seocraft.api.shared.redis;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;

public class RedisChannel<O> implements Channel<O> {
    private String name;
    private TypeToken<O> type;

    private RedisClient redis;
    private JedisPubSub pubSub;

    private Gson gson;

    private Deque<ChannelListener<O>> channelListeners;

    private String serverChannelId = UUID.randomUUID().toString();

    RedisChannel(String name, TypeToken<O> type, RedisClient redis, Gson gson, ExecutorService executorService) {
        this.name = name;
        this.type = type;

        this.redis = redis;

        this.gson = gson;

        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                JsonParser parser = new JsonParser();
                JsonObject wrappedObject = parser.parse(message).getAsJsonObject();

                String id = wrappedObject.get("id").getAsString();

                if(id.equals(serverChannelId)){
                    return;
                }

                O object = gson.fromJson(wrappedObject.getAsJsonObject("object"), type.getType());

                channelListeners.forEach(listener -> {
                    listener.receiveMessage(object);
                });
            }
        };

        executorService.submit(() -> {
            try (Jedis jedis = redis.getPool().getResource()) {
                jedis.subscribe(pubSub, name);
            }
        });

        channelListeners = new ConcurrentLinkedDeque<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeToken<O> getType() {
        return type;
    }

    @Override
    public void sendMessage(O object) {
        try (Jedis jedis = redis.getPool().getResource()) {

            JsonElement jsonObject = gson.toJsonTree(object);

            JsonObject wrapper = new JsonObject();

            wrapper.addProperty("id", serverChannelId);
            wrapper.add("object", jsonObject);

            jedis.publish(name, wrapper.toString());
        }
    }

    @Override
    public void registerListener(ChannelListener<O> listener) {
        channelListeners.add(listener);
    }
}