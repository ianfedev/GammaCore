package net.seocraft.api.bukkit.lobby.selector;

import org.bukkit.creator.hologram.Hologram;
import org.jetbrains.annotations.NotNull;

public interface SelectorHologramUpdater {

    void scheduleNewHologramUpdater(@NotNull String gamemodeId, @NotNull String language, @NotNull Hologram hologram);

    void updateAll();

    void scheduleUpdater();

}
