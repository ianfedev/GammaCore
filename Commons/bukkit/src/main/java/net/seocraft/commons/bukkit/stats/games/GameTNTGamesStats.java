package net.seocraft.commons.bukkit.stats.games;

import net.seocraft.api.bukkit.stats.games.TNTGamesStats;

import java.beans.ConstructorProperties;

public class GameTNTGamesStats implements TNTGamesStats {

    private int runDoubleJump;
    private int coins;

    @ConstructorProperties({
            "runDoubleJump",
            "coins"
    })
    public GameTNTGamesStats(int runDoubleJump, int coins) {
        this.runDoubleJump = runDoubleJump;
        this.coins = coins;
    }

    @Override
    public int getRunDoubleJump() {
        return this.runDoubleJump;
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
}
