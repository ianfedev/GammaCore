package net.seocraft.api.bukkit.game.gamemode.model;

import net.seocraft.api.bukkit.game.subgame.SubGamemode;
import net.seocraft.api.shared.model.Model;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Gamemode extends Model {

    @NotNull String getName();

    @NotNull String getLobbyGroup();

    @NotNull ItemStack getNavigatorIcon();

    int getNavigatorSlot();

    @NotNull List<SubGamemode> getSubGamemodes();

}
