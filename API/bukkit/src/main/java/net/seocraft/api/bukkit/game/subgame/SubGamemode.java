package net.seocraft.api.bukkit.game.subgame;

import net.seocraft.api.shared.model.Model;
import net.seocraft.api.shared.serialization.model.FieldName;
import net.seocraft.api.shared.serialization.model.ImplementedBy;
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
