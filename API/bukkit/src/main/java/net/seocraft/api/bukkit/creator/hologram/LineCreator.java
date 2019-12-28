package net.seocraft.api.bukkit.creator.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface LineCreator {

    @NotNull HologramLine createLine(@NotNull String message, @NotNull Player player, @NotNull Location location, int position);
}
