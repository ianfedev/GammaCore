package net.seocraft.commons.bukkit.stats;

import net.seocraft.api.bukkit.stats.Stats;
import net.seocraft.api.bukkit.stats.games.SkyWarsStats;
import net.seocraft.api.bukkit.stats.games.TNTGamesStats;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class GameStats implements Stats {

    @NotNull private String id;
    @NotNull private String owner;
    @NotNull private SkyWarsStats skyWarsStats;
    @NotNull private TNTGamesStats TNTGamesStats;

    @ConstructorProperties({
            "_id",
            "user",
            "skyWars",
            "tntGames"
    })
    public GameStats(@NotNull String id, @NotNull String owner, @NotNull SkyWarsStats skyWarsStats, @NotNull TNTGamesStats TNTGamesStats) {
        this.id = id;
        this.owner = owner;
        this.skyWarsStats = skyWarsStats;
        this.TNTGamesStats = TNTGamesStats;
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

    @Override
    public @NotNull TNTGamesStats getTNTGamesStats() {
        return this.TNTGamesStats;
    }


}
