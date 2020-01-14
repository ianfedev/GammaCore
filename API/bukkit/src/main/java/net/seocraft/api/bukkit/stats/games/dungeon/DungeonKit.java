package net.seocraft.api.bukkit.stats.games.dungeon;

import net.seocraft.api.bukkit.stats.games.dungeon.partial.DungeonEnchantment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface DungeonKit {

    @NotNull ItemStack getItem();

    void setItem(@NotNull String item);

    @NotNull Set<Material> getUpgrades();

    void setUpgrades(Set<Material> upgrades);

    int getCurrentMaterial();

    void setCurrentMaterial(int material);

    @NotNull Set<DungeonEnchantment> getPermanentEnchantments();

    void setPermanentEnchantments(@NotNull Set<DungeonEnchantment> enchantments);

}
