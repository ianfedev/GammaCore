package net.seocraft.commons.bukkit.stats.games.dungeon;

import net.seocraft.api.bukkit.stats.games.dungeon.DungeonKit;
import net.seocraft.api.bukkit.stats.games.dungeon.partial.DungeonEnchantment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

public class GameDungeonKit implements DungeonKit {

    @NotNull private ItemStack item;
    @NotNull private Set<Material> upgrades;
    private int currentMaterial;
    @NotNull private Set<DungeonEnchantment> permanentEnchantments;

    @ConstructorProperties({
            "item",
            "upgrades",
            "currentMaterial",
            "parmanentEnchantments"
    })
    public GameDungeonKit(@NotNull ItemStack item, @NotNull Set<Material> upgrades, int currentMaterial, @NotNull Set<DungeonEnchantment> permanentEnchantments) {
        this.item = item;
        this.upgrades = upgrades;
        this.currentMaterial = currentMaterial;
        this.permanentEnchantments = permanentEnchantments;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.item;
    }

    @Override
    public void setItem(@NotNull ItemStack item) {
        this.item = item;
    }

    @Override
    public @NotNull Set<Material> getUpgrades() {
        return this.upgrades;
    }

    @Override
    public void setUpgrades(@NotNull Set<Material> upgrades) {
        this.upgrades = upgrades;
    }

    @Override
    public int getCurrentMaterial() {
        return this.currentMaterial;
    }

    @Override
    public void setCurrentMaterial(int material) {
        this.currentMaterial = material;
    }

    @Override
    public @NotNull Set<DungeonEnchantment> getPermanentEnchantments() {
        return this.permanentEnchantments;
    }

    @Override
    public void setPermanentEnchantments(@NotNull Set<DungeonEnchantment> enchantments) {
        this.permanentEnchantments = enchantments;
    }
}
