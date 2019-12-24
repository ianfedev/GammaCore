package net.seocraft.api.bukkit.creator.npc.navigation;

import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Navigator {

    @NotNull Optional<Path> findPath(@NotNull NPC npc, @NotNull Location destination, double speed, double range) throws IllegalArgumentException;

}