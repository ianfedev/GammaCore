package net.seocraft.commons.bukkit.creator.hologram;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.seocraft.api.bukkit.creator.hologram.HologramLine;
import net.seocraft.api.bukkit.creator.hologram.LineCreator;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CraftLineCreator implements LineCreator {

    @Override
    public @NotNull HologramLine createLine(@NotNull String message, @NotNull Player player, @NotNull Location location, int position) {
        return new CraftHologramLine(message, createStand(message, player, location, position));
    }

    private int createStand(@NotNull String message, @NotNull Player player, @NotNull Location location, int position) {
        Location standLocation = location.clone();

        WorldServer s = ((CraftWorld) standLocation.getWorld()).getHandle();
        EntityArmorStand stand = new EntityArmorStand(s);

        stand.setLocation(standLocation.getX(), (standLocation.getY() - (position - 1) * 0.25), standLocation.getZ(), 0, 0);
        stand.setCustomName(message);
        stand.setCustomNameVisible(true);
        stand.setGravity(true);
        stand.setSmall(true);
        stand.setInvisible(true);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(stand);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        return stand.getId();
    }
}
