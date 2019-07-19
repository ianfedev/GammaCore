package net.seocraft.api.shared.cooldown;

import com.google.inject.Inject;
import net.seocraft.api.shared.redis.RedisClientImpl;
import org.jetbrains.annotations.NotNull;

public class CooldownManagerImp implements CooldownManager {

    @Inject private RedisClientImpl client;

    private static final String PREFIX = "cooldown";

    @Override
    public void createCooldown(@NotNull String id, @NotNull String cooldownType, int cooldownSeconds) {
        String type = cooldownType.toLowerCase();
        this.client.setString(
                PREFIX + ":" + type + ":" + id,
                type
        );
        this.client.setExpiration(
                PREFIX + ":" + type + ":" + id,
                cooldownSeconds
        );
    }

    @Override
    public boolean hasCooldown(@NotNull String id, @NotNull String cooldownType) {
        return this.client.existsKey(PREFIX + ":" + cooldownType.toLowerCase() + ":" + id);
    }

}
