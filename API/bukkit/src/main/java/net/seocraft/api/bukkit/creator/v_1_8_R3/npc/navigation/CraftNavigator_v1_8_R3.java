package net.seocraft.api.bukkit.creator.v_1_8_R3.npc.navigation;

import net.minecraft.server.v1_8_R3.*;
import net.seocraft.api.bukkit.creator.npc.entity.NPCEntity;
import net.seocraft.api.bukkit.creator.npc.navigation.Navigator;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class CraftNavigator_v1_8_R3 implements Navigator {

    @Override
    public @NotNull Optional<net.seocraft.api.bukkit.creator.npc.navigation.Path> findPath(@NotNull net.seocraft.api.bukkit.creator.npc.NPC npc, @NotNull Location destination, double speed, double range) throws IllegalArgumentException {
        if (speed > 1) throw new IllegalArgumentException("Speed must not be higher than 1");

        double destinationX = destination.getX();
        double destinationY = destination.getY();
        double destinationZ = destination.getZ();

        int i = (int) (range + 8.0D);

        PathEntity pathEntity = null;

        if (npc instanceof NPCEntity && ((NPCEntity) npc).getPathfinder().isPresent()) {
            BlockPosition positionFrom = new BlockPosition((Entity) ((NPCEntity) npc).getEntity());
            BlockPosition positionTo = new BlockPosition(destinationX, destinationY, destinationZ);

            ChunkCache chunkCache = new ChunkCache(((CraftWorld) npc.getLocation().getWorld()).getHandle(), positionFrom.a(-i, -i, -i), positionTo.a(i, i, i), 0);

            if (!((NPCEntity) npc).getPathfinder().isPresent()) return Optional.empty();

            pathEntity = ((Pathfinder) ((NPCEntity) npc).getPathfinder().get()).a(chunkCache, (Entity) ((NPCEntity) npc).getEntity(), positionTo, (float) range);
        }

        if (pathEntity != null) return Optional.of(new CraftPath_v1_8_R3(npc, destination, pathEntity, speed));
        return Optional.empty();
    }
}