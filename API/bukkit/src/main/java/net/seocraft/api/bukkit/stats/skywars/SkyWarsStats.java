package net.seocraft.api.bukkit.stats.skywars;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface SkyWarsStats {

    int getKills();

    @JsonIgnore
    void addKill();

    int getDeaths();

    @JsonIgnore
    void addDeath();

    int getCoins();

    @JsonIgnore
    void addCoins(int coins);

    @JsonIgnore
    void removeCoins(int coins);

    Set<String> getKits();

    @JsonIgnore
    void addKit(@NotNull String kit);
}