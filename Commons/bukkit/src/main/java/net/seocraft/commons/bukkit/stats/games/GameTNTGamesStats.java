package net.seocraft.commons.bukkit.stats.games;

import net.seocraft.api.bukkit.stats.games.TNTGamesStats;

public class GameTNTGamesStats implements TNTGamesStats {

    private int TNTRunDoubleJump;
    private int coins;

    public GameTNTGamesStats(int TNTRunDoubleJump, int coins) {
        this.TNTRunDoubleJump = TNTRunDoubleJump;
        this.coins = coins;
    }

    @Override
    public int getTNTRunDoubleJump() {
        return this.TNTRunDoubleJump;
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
