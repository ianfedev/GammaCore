package net.seocraft.api.core.cooldown;

import org.jetbrains.annotations.NotNull;

public interface CooldownManager {

    void createCooldown(@NotNull String id, @NotNull String cooldownType, int cooldownSeconds);

    boolean hasCooldown(@NotNull String id, @NotNull String cooldownType);

    //TODO: Create cooldown seconds left

    // int getRemainingTime(@NotNull String getId, @NotNull String cooldownType);

}