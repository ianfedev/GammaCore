package net.seocraft.commons.bukkit.creator.hologram;

import net.seocraft.api.bukkit.creator.hologram.Hologram;
import net.seocraft.api.bukkit.creator.hologram.HologramLine;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CraftHologram implements Hologram {

    @NotNull private UUID id;
    @NotNull private List<HologramLine> lines;
    @NotNull private Location location;

    public CraftHologram(@NotNull List<String> lines, @NotNull Location location) {
        this.id = UUID.randomUUID();
        this.lines = new ArrayList<>();
        lines.forEach(this::addLine);
        this.location = location;
    }

    public CraftHologram(@NotNull Location location) {
        this.id = UUID.randomUUID();
        this.lines = new ArrayList<>();
        this.location = location;
    }

    @Override
    public @NotNull UUID getId() {
        return this.id;
    }

    @Override
    public @NotNull List<HologramLine> getLines() {
        return this.lines;
    }

    @Override
    public @NotNull Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(@NotNull Location location) {

    }

    @Override
    public void addLine(@NotNull String message) {
        this.lines.add(
                new CraftHologramLine(
                        message,
                        this.createStand(message, (this.lines.size() + 1))
                )
        );
    }

    @Override
    public void removeLine(int line) {
        if (line > 0) {
            CraftHologramLine hologramLine = (CraftHologramLine) this.getLines().get(line - 1);
            hologramLine.getStand().remove();
            this.getLines().remove(line - 1);
        }
    }

    @Override
    public void setLine(int line, @NotNull String message) {
        if (this.lines.size() < line) {
            this.removeLine(line);
        }
    }

    private @NotNull ArmorStand createStand(@NotNull String message, int position) {
        Location armorLocation = this.location;
        armorLocation.setY(this.location.getY() - ((position - 1) * 2));
        ArmorStand stand = (ArmorStand) this.location.getWorld().spawnEntity(armorLocation, EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setCanPickupItems(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(message);
        stand.setVisible(false);
        return stand;
    }

}
