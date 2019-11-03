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
    @NotNull private Set<String> cages;
    @NotNull private String currentKit;
    @NotNull private String currentCage;

    @ConstructorProperties({
            "kills",
            "deaths",
            "coins",
            "kits",
            "cages",
            "currentKit",
            "currentCage"
    })
    public GameSkyWarsStats(int kills, int deaths, int coins, @NotNull Set<String> kits, @NotNull Set<String> cages, @NotNull String currentKit, @NotNull String currentCage) {
        this.kills = kills;
        this.deaths = deaths;
        this.coins = coins;
        this.kits = kits;
        this.cages = cages;
        this.currentKit = currentKit;
        this.currentCage = currentCage;
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
    public @NotNull Set<String> getCages() {
        return this.cages;
    }

    @Override
    public @NotNull String getCurrentCage() {
        return this.currentCage;
    }

    @Override
    public @NotNull String getCurrentKit() {
        return this.currentKit;
    }

    @Override
    public void addCage(@NotNull String cage) {
        this.cages.add(cage);
    }

    @Override
    public void addKit(@NotNull String kit) {
        this.kits.add(kit);
    }
}
