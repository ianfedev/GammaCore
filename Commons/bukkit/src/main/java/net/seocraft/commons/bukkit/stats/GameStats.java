package net.seocraft.commons.bukkit.stats;

import net.seocraft.api.bukkit.stats.Stats;
import net.seocraft.api.bukkit.stats.skywars.SkyWarsStats;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class GameStats implements Stats {

    @NotNull private String id;
    @NotNull private String owner;
    @NotNull private SkyWarsStats skyWarsStats;

    @ConstructorProperties({
            "owner",
            "skyWars"
    })
    public GameStats(@NotNull String id, @NotNull String owner, @NotNull SkyWarsStats skyWarsStats) {
        this.id = id;
        this.owner = owner;
        this.skyWarsStats = skyWarsStats;
    }

    @Override
    public @NotNull String getId() {
        return this.id;
    }

    @Override
    public @NotNull String getOwner() {
        return this.owner;
    }

    @Override
    public @NotNull SkyWarsStats getSkyWarsStats() {
        return this.skyWarsStats;
    }


}
