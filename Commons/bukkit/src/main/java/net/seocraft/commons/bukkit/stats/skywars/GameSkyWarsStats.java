package net.seocraft.commons.bukkit.stats.skywars;

import net.seocraft.api.bukkit.stats.skywars.SkyWarsStats;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

public class GameSkyWarsStats implements SkyWarsStats {

    private int kills;
    private int deaths;
    private int coins;
    @NotNull private Set<String> kits;

    @ConstructorProperties({
            "kills",
            "deaths",
            "coins",
            "kits"
    })
    public GameSkyWarsStats(int kills, int deaths, int coins, @NotNull Set<String> kits) {
        this.kills = kills;
        this.deaths = deaths;
        this.coins = coins;
        this.kits = kits;
    }

    @Override
    public int getKills() {
        return this.kills;
    }

    @Override
    public void addKill() {
        this.kills++;
    }

    @Override
    public int getDeaths() {
        return this.deaths;
    }

    @Override
    public void addDeath() {
        this.deaths++;
    }

    @Override
    public int getCoins() {
        return this.coins;
    }

    @Override
    public void addCoins(int coins) {
        this.coins += coins;
    }

    @Override
    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    @Override
    public @NotNull Set<String> getKits() {
        return this.kits;
    }

    @Override
    public void addKit(@NotNull String kit) {
        this.kits.add(kit);
    }
}
