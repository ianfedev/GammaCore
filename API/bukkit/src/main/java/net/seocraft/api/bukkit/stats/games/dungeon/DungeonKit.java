package net.seocraft.api.bukkit.stats.games.dungeon;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.bukkit.stats.games.dungeon.partial.DungeonEnchantment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface DungeonKit {

    @JsonProperty("material")
    int getCurrentMaterial();

    @JsonProperty("pe")
    @NotNull Set<DungeonEnchantment> getPermanentEnchantments();

}
