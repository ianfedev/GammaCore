package net.seocraft.commons.core.redis.messager;

import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RedisClientConfiguration {

    @NotNull private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 6379;
    private static final int DATABASE = 0;
    @NotNull private static final String PASSWORD = "&g4MErwHo#Yx6A(RLw*Ci3K&[hyn%9A@";

    public @NotNull String getAddress() {
        return ADDRESS;
    }

    public int getPort() {
        return PORT;
    }

    public int getDatabase() {
        return DATABASE;
    }

    public @NotNull String getPassword() {
        return PASSWORD;
    }
}
