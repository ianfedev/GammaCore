package net.seocraft.api.bukkit.lobby.selector;

import org.bukkit.creator.hologram.Hologram;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface SelectorHologramUpdater {

    @NotNull
    Optional<Hologram> getHologram(@NotNull String gamemodeId, @NotNull String language);

    void scheduleNewHologramUpdater(@NotNull String gamemodeId, @NotNull String language, @NotNull Hologram hologram);

    void updateAll();

    void scheduleUpdater();

}
