package net.seocraft.api.bukkit.stats.games;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface TNTGamesStats {

    int getTNTRunDoubleJump();

    int getCoins();

    @JsonIgnore
    void addCoins(int coins);

    @JsonIgnore
    void removeCoins(int coins);
}
