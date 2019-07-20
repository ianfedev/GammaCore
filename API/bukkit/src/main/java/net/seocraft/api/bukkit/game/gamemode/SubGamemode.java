package net.seocraft.api.bukkit.game.gamemode;

import net.seocraft.api.bukkit.old.game.subgame.SubGamemodeImp;
import net.seocraft.api.core.storage.Model;
import net.seocraft.api.core.old.serialization.model.FieldName;
import net.seocraft.api.core.old.serialization.model.ImplementedBy;
import org.jetbrains.annotations.NotNull;

@ImplementedBy(SubGamemodeImp.class)
public interface SubGamemode extends Model {

    @NotNull String getName();

    @NotNull String getScoreboard();

    @FieldName("selectable_map")
    boolean canSelectMap();

    @FieldName("min_players")
    int getMinPlayers();

    @FieldName("max_players")
    int getMaxPlayers();

    @NotNull String getPermission();

    @FieldName("group")
    @NotNull String getServerGroup();

}
