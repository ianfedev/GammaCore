package net.seocraft.api.bukkit.creator.hologram;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface Hologram {

    /**
     * @return an unique identifier for the hologram
     */
    @NotNull UUID getId();

    /**
     * @return the quantity of lines of the hologram
     */
    @NotNull List<HologramLine> getLines();

    /**
     * @return the location of the hologram
     */
    @NotNull Location getLocation();

    /**
     * @param location establishes a new location for the hologram
     */
    void setLocation(@NotNull Location location);

    /**
     * @param message add a new line to the hologram
     */
    void addLine(@NotNull String message);

    /**
     * @param line to remove (Can not be zero)
     */
    void removeLine(int line);

    /**
     * @param line to be updated (Can not be zero)
     * @param message to be updated
     */
    void setLine(int line, @NotNull String message);

}
