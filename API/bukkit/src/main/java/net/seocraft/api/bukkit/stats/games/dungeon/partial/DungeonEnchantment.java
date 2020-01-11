package net.seocraft.api.bukkit.stats.games.dungeon.partial;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public interface DungeonEnchantment {

    @NotNull Enchantment getEnchantment();

    void setEnchantment(@NotNull Enchantment enchantment);

    int getLevel();

    void setLevel(int level);

}
