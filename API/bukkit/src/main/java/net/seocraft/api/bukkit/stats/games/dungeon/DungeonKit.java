package net.seocraft.api.bukkit.stats.games.dungeon;

import net.seocraft.api.bukkit.stats.games.dungeon.partial.DungeonEnchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface DungeonKit {

    @NotNull String getItem();

    void setItem(@NotNull String item);

    @NotNull Set<String> getUpgrades();

    void setUpgrades(Set<String> upgrades);

    int getCurrentMaterial();

    void setCurrentMaterial(int material);

    @NotNull Set<DungeonEnchantment> getPermanentEnchantments();

    void setPermanentEnchantments(@NotNull Set<DungeonEnchantment> enchantments);

}
