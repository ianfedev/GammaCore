package net.seocraft.api.bukkit.creator.hologram;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface LineRemover {

    void removeLine(@NotNull Player player, int packetId);
}
