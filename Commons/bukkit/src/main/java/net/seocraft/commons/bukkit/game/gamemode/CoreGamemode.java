package net.seocraft.commons.bukkit.game.gamemode;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

public class CoreGamemode implements Gamemode {

    @NotNull private String id;
    @NotNull private String name;
    @NotNull private String scoreboard;
    @NotNull private String lobbyGroup;
    @NotNull private String navigatorIcon;
    private int navigatorSlot;
    @NotNull private Set<SubGamemode> subGamemodes;

    @ConstructorProperties({"_id", "name", "scoreboard", "lobby", "navigator", "slot", "sub_types"})
    public CoreGamemode(@NotNull String id, @NotNull String name, @NotNull String scoreboard, @NotNull String lobbyGroup, @NotNull String navigatorIcon, int navigatorSlot, @NotNull Set<SubGamemode> subGamemodes) {
        this.id = id;
        this.name = name;
        this.scoreboard = scoreboard;
        this.lobbyGroup = lobbyGroup;
        this.navigatorIcon = navigatorIcon;
        this.navigatorSlot = navigatorSlot;
        this.subGamemodes = subGamemodes;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public @NotNull String getLobbyGroup() {
        return this.lobbyGroup;
    }

    @Override
    public @NotNull String getNavigatorIcon() {
        return this.navigatorIcon;
    }

    @Override
    public int getNavigatorSlot() {
        return this.navigatorSlot;
    }

    @Override
    public @NotNull Set<SubGamemode> getSubGamemodes() {
        return this.subGamemodes;
    }

    @Override
    public @NotNull ItemStack obtainStackItem() {
        return new ItemStack(
                Material.getMaterial(getNavigatorIcon().toUpperCase()),
                1
        );
    }
}
