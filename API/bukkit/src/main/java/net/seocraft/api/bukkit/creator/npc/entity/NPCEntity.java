package net.seocraft.api.bukkit.creator.npc.entity;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface NPCEntity {

    @NotNull Object getEntity();

    @NotNull Optional<Object> getPathfinder();

    void move(double x, double y, double z);

    void checkMovement(double x, double y, double z);

    void updateToPlayer(@NotNull Player player);

}