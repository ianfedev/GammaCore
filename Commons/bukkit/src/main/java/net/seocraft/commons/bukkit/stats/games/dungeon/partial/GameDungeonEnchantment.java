package net.seocraft.commons.bukkit.stats.games.dungeon.partial;

import net.seocraft.api.bukkit.stats.games.dungeon.partial.DungeonEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class GameDungeonEnchantment implements DungeonEnchantment {

    @NotNull private Enchantment enchantment;
    private int level;

    @ConstructorProperties({
            "enchantment",
            "level"
    })
    public GameDungeonEnchantment(@NotNull Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    @Override
    public @NotNull Enchantment getEnchantment() {
        return this.enchantment;
    }

    @Override
    public void setEnchantment(@NotNull Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

}
