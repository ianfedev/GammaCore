package net.seocraft.commons.bukkit.creator.hologram;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.seocraft.api.bukkit.creator.hologram.LineRemover;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CraftLineRemover implements LineRemover {
    @Override
    public void removeLine(@NotNull Player player, int packetId) {
        PacketPlayOutEntityDestroy deadArmorstand = new PacketPlayOutEntityDestroy(packetId);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(deadArmorstand);
    }
}
