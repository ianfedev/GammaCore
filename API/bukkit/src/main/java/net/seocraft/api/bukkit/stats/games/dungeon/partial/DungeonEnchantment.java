package net.seocraft.api.bukkit.stats.games.dungeon.partial;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public interface DungeonEnchantment {

    @NotNull String getEnchantment();

    void setEnchantment(@NotNull String enchantment);

    int getLevel();

    void setLevel(int level);

}
