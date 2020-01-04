package net.seocraft.api.bukkit.cloud;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ServerRedirector {

    void redirectPlayer(@NotNull Gamemode gamemode, @Nullable SubGamemode subGamemode, @NotNull Player player, boolean perk);
}
