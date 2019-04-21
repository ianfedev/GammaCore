package net.seocraft.api.shared.redis;

import com.google.inject.Singleton;

@Singleton
class RedisClientConfiguration {

    private static final String ADDRESS = "127.0.0.1";
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
