package net.seocraft.commons.bukkit.creator.hologram;

import net.seocraft.api.bukkit.creator.hologram.Hologram;
import net.seocraft.api.bukkit.creator.hologram.HologramLine;
import net.seocraft.api.bukkit.creator.hologram.LineCreator;
import net.seocraft.api.bukkit.creator.hologram.LineRemover;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CraftHologram implements Hologram {

    private LineCreator lineCreator = new CraftLineCreator();
    private LineRemover lineRemover = new CraftLineRemover();
    @NotNull private UUID id;
    @NotNull private Player player;
    @NotNull private List<HologramLine> lines;
    @NotNull private Location location;

    public CraftHologram(@NotNull List<String> lines, @NotNull Location location, @NotNull Player player) {
        this.id = UUID.randomUUID();
        this.lines = new ArrayList<>();
        lines.forEach(this::addLine);
        this.location = location;
        this.player = player;
    }

    public CraftHologram(@NotNull Location location, @NotNull Player player) {
        this.id = UUID.randomUUID();
        this.lines = new ArrayList<>();
        this.location = location;
        this.player = player;
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
    public @NotNull Player getPlayer() {
        return this.player;
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
                this.lineCreator.createLine(message, player, location, (this.lines.size() + 1))
        );
    }

    @Override
    public void addSpace() {
        // Create space
    }

    @Override
    public void removeLine(int line) {
        if (line > 0) {
            CraftHologramLine hologramLine = (CraftHologramLine) this.getLines().get(line - 1);
            this.lineRemover.removeLine(player, hologramLine.getEntityId());
            this.getLines().remove(line - 1);
        }
    }

    @Override
    public void setLine(int line, @NotNull String message) {
        if (this.lines.size() < line) {
            this.removeLine(line);
        }
    }

}
