package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface SpectatorManager {

    void enableSpectatorMode(@NotNull User user, @NotNull Player player);
}
