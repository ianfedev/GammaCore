package net.seocraft.api.bukkit.creator.npc.navigation;

import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Path {

    @NotNull NPC getNpc();

    @NotNull Location getDestination();

    @NotNull Object getPathEntity();

    @NotNull Vector getCurrentPoint();

    double getSpeed();

    double getProgress();

    boolean update();

}