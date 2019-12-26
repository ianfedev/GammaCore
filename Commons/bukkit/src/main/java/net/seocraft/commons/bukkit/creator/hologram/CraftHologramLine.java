package net.seocraft.commons.bukkit.creator.hologram;

import net.seocraft.api.bukkit.creator.hologram.HologramLine;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

public class CraftHologramLine implements HologramLine {

    @NotNull private String text;
    @NotNull private ArmorStand stand;

    public CraftHologramLine(@NotNull String text, @NotNull ArmorStand stand) {
        this.text = text;
        this.stand = stand;
    }

    @Override
    public @NotNull String getText() {
        return this.text;
    }

    public @NotNull ArmorStand getStand() {
        return stand;
    }
}
