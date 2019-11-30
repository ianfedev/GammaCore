package net.seocraft.api.bukkit.stats.games;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface TNTGamesStats {

    int getRunDoubleJump();

    int getCoins();

    @JsonIgnore
    void addCoins(int coins);

    @JsonIgnore
    void removeCoins(int coins);
}
