package net.seocraft.api.bukkit.stats.games.dungeon;

import net.seocraft.api.bukkit.stats.games.dungeon.partial.DungeonEnchantment;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

public class GameDungeonKit implements DungeonKit {

    private int material;
    private Set<DungeonEnchantment> permanentEnchantments;

    @ConstructorProperties({
            "material",
            "pe"
    })
    public GameDungeonKit(int material, Set<DungeonEnchantment> permanentEnchantments) {
        this.material = material;
        this.permanentEnchantments = permanentEnchantments;
    }

    @Override
    public int getCurrentMaterial() {
        return this.material;
    }

    @Override
    public @NotNull Set<DungeonEnchantment> getPermanentEnchantments() {
        return this.permanentEnchantments;
    }
}
