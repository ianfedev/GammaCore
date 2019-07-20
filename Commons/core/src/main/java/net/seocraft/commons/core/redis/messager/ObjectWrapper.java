package net.seocraft.commons.core.redis.messager;

public class ObjectWrapper<O> {

    private final O object;

    private final String serverSenderId;

    public ObjectWrapper(O object, String serverSenderId) {
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
