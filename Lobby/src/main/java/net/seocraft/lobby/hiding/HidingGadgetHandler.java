package net.seocraft.lobby.hiding;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface HidingGadgetHandler {

    void enableHiding(@NotNull Player player);

    void disableHiding(@NotNull Player player);

}
