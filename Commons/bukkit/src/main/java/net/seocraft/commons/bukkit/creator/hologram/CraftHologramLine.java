package net.seocraft.commons.bukkit.creator.hologram;

import net.seocraft.api.bukkit.creator.hologram.HologramLine;
import org.jetbrains.annotations.NotNull;

public class CraftHologramLine implements HologramLine {

    @NotNull private String text;
    private int entityId;

    public CraftHologramLine(@NotNull String text, int entityId) {
        this.text = text;
        this.entityId = entityId;
    }

    @Override
    public @NotNull String getText() {
        return this.text;
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

}
