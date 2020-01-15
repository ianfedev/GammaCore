package net.seocraft.commons.bukkit.stats.games.dungeon;

import net.seocraft.api.bukkit.stats.games.dungeon.DungeonKit;
import net.seocraft.api.bukkit.stats.games.dungeon.DungeonStats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;

public class GameDungeonStats implements DungeonStats {

    private int squamas;
    private int crowns;
    private double experience;
    @NotNull private DungeonKit helmet;
    @NotNull private DungeonKit chestplate;
    @NotNull private DungeonKit leggings;
    @NotNull private DungeonKit boots;
    @NotNull private DungeonKit sword;
    @NotNull private DungeonKit bow;

    @ConstructorProperties({
            "squamas",
            "crowns",
            "experience",
            "helmet",
            "chestplate",
            "leggings",
            "boots",
            "sword",
            "bow"
    })
    public GameDungeonStats(int squamas, int crowns, double experience, @NotNull DungeonKit helmet, @NotNull DungeonKit chestplate, @NotNull DungeonKit leggings, @NotNull DungeonKit boots, @NotNull DungeonKit sword, @NotNull DungeonKit bow) {
        this.squamas = squamas;
        this.crowns = crowns;
        this.experience = experience;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.sword = sword;
        this.bow = bow;
    }


    @Override
    public int getSquamas() {
        return this.squamas;
    }

    @Override
    public void addSquamas(int squamas) {
        this.squamas += squamas;
    }

    @Override
    public void removeSquamas(int squamas) {
        this.squamas -= squamas;
    }

    @Override
    public int getCrowns() {
        return this.crowns;
    }

    @Override
    public void addCrowns(int crowns) {
        this.crowns += crowns;
    }

    @Override
    public void removeCrowns(int crowns) {
        this.crowns -= crowns;
    }

    @Override
    public double getExperience() {
        return this.experience;
    }

    @Override
    public void addExperience(double experience) {
        this.experience += experience;
    }

    @Override
    public int getLevel() {

        if(experience < 100) return 0;

        int xp = 100;
        int level = 0;

        for(int i = 1; i < 101; i++) {
            xp += (xp/10);
            if(experience < xp) {
                return i;
            }
        }

        for(int i = 100; i < 201; i++) {
            xp += (xp/20);
            if(experience < xp) {
                return i;
            }
        }

        return level;
    }

    @Override
    public @NotNull DungeonKit getHelmet() {
        return this.helmet;
    }

    @Override
    public void setHelmet(@NotNull DungeonKit kit) {
        this.helmet = kit;
    }


    @Override
    public @NotNull DungeonKit getChestplate() {
        return this.chestplate;
    }

    @Override
    public void setChestplate(@NotNull DungeonKit kit) {
        this.chestplate = kit;
    }

    @Override
    public @NotNull DungeonKit getLeggings() {
        return this.leggings;
    }

    @Override
    public void setLeggings(@NotNull DungeonKit kit) {
        this.leggings = kit;
    }


    @Override
    public @NotNull DungeonKit getBoots() {
        return this.boots;
    }

    @Override
    public void setBoots(@NotNull DungeonKit kit) {
        this.boots = kit;
    }


    @Override
    public @NotNull DungeonKit getSword() {
        return this.sword;
    }

    @Override
    public void setSword(@NotNull DungeonKit kit) {
        this.sword = kit;
    }


    @Override
    public @NotNull DungeonKit getBow() {
        return this.bow;
    }

    @Override
    public void setBow(@NotNull DungeonKit kit) {
        this.bow = kit;
    }


}
