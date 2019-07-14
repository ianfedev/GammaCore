package net.seocraft.api.bukkit.game.gamemode.model;

import net.seocraft.api.bukkit.game.subgame.SubGamemode;
import net.seocraft.api.shared.model.Model;
import net.seocraft.api.shared.serialization.model.FieldName;
import net.seocraft.api.shared.serialization.model.IgnoreMethod;
import net.seocraft.api.shared.serialization.model.ImplementedBy;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ImplementedBy(GamemodeImp.class)
public interface Gamemode extends Model {

    @NotNull String getName();

    @NotNull String getScoreboard();

    @FieldName("lobby")
    @NotNull String getLobbyGroup();

    @FieldName("navigator")
    @NotNull String getNavigatorIcon();

    @FieldName("slot")
    int getNavigatorSlot();

    @FieldName("sub_types")
    @NotNull List<SubGamemode> getSubGamemodes();

    @IgnoreMethod
    @NotNull ItemStack obtainStackItem();
}
