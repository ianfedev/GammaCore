package net.seocraft.api.bukkit.creator.intercept;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PacketManager {

    @NotNull List<PacketListener> getPacketListeners();

    void addPacketListener(@NotNull PacketListener packetListener);

    void injectPlayer(@NotNull Player player);

    void uninjectPlayer(@NotNull Player player);

}
