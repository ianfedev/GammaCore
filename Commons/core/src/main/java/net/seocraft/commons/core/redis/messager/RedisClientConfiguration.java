package net.seocraft.commons.core.redis.messager;

import com.google.inject.Singleton;

@Singleton
class RedisClientConfiguration {

    private static final String ADDRESS = "51.38.191.118";
    private static final Integer PORT = 6379;
    private static final Integer DATABASE = 0;
    private static final String PASSWORD = "";

    String getAddress() {
        return ADDRESS;
    }

    Integer getPort() {
        return PORT;
    }

    Integer getDatabase() {
        return DATABASE;
    }

    String getPassword() {
        return PASSWORD;
    }
}
