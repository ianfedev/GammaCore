package net.seocraft.commons.core.redis.messager;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectWrapper<O> {

    private final O object;
    private final String serverSenderId;

    @JsonCreator
    public ObjectWrapper(@JsonProperty("object") O object, @JsonProperty("serverSenderId") String serverSenderId) {
        this.object = object;
        this.serverSenderId = serverSenderId;
    }

    public O getObject() {
        return object;
    }

    public String getServerSenderId() {
        return serverSenderId;
    }
}
