package net.seocraft.api.bukkit.game.gamemode;

import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;

public interface SubGamemode extends Model {

    @NotNull String getName();

    boolean canSelectMap();

    int getMinPlayers();

    int getMaxPlayers();

    @NotNull String getPermission();

    @NotNull String getServerGroup();

}
