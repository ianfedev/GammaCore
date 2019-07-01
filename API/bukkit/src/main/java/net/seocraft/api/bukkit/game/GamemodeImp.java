package net.seocraft.api.bukkit.game;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GamemodeImp implements Gamemode {

    @SerializedName("_id") @NotNull private String id;
    @NotNull private String name;
    @SerializedName("lobby") @NotNull private String lobbyGroup;
    @SerializedName("navigator") @NotNull private String navigatorIcon;
    @SerializedName("slot") private int navigatorSlot;
    @NotNull private List<SubGamemodeImp> subGamemodes;

    public GamemodeImp(@NotNull String id, @NotNull String name, @NotNull String lobbyGroup, @NotNull String navigatorIcon, int navigatorSlot, @NotNull List<SubGamemodeImp> subGamemodes) {
        this.id = id;
        this.name = name;
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
    public @NotNull String getLobbyGroup() {
        return this.lobbyGroup;
    }

    @Override
    public @NotNull ItemStack getNavigatorIcon() {
        return new ItemStack(
                Material.getMaterial(this.navigatorIcon.toUpperCase()),
                1
        );
    }

    @Override
    public int getNavigatorSlot() {
        return this.navigatorSlot;
    }

    @Override
    public @NotNull List<SubGamemode> getSubGamemodes() {
        return new ArrayList<>(this.subGamemodes);
    }

}
