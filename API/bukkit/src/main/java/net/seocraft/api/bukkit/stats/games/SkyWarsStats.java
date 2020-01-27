package net.seocraft.api.bukkit.stats.games;

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

    @NotNull Set<String> getKits();

    @NotNull String getCurrentKit();

    void setCurrentKit(@NotNull String kit);

    @JsonIgnore
    void addKit(@NotNull String kit);

    @NotNull Set<String> getCages();

    @NotNull String getCurrentCage();

    void setCurrentCage(@NotNull String cage);

    @JsonIgnore
    void addCage(@NotNull String cage);
}