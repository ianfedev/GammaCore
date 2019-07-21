package net.seocraft.api.bukkit.lobby;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface HidingGadgetManager {

    void enableHiding(@NotNull Player player);

    void disableHiding(@NotNull Player player);

}
