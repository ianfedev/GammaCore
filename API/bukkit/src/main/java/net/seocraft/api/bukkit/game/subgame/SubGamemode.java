package net.seocraft.api.bukkit.game.subgame;

import net.seocraft.api.shared.model.Model;
import org.jetbrains.annotations.NotNull;

public interface SubGamemode extends Model {

    @NotNull String getName();

    boolean canSelectMap();

    int getMinPlayers();

    int getMaxPlayers();

    @NotNull String getPermission();

    @NotNull String getServerGroup();

}
