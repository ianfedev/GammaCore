package net.seocraft.commons.bukkit.game.gamemode;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

@JsonSerialize(as = SubGamemode.class)
public class CoreSubGamemode implements SubGamemode {

    @NotNull private String id;
    @NotNull private String name;
    @NotNull private String scoreboard;
    private boolean selectMap;
    private int minPlayers;
    private int maxPlayers;
    @NotNull private String permission;
    @NotNull private String serverGroup;

    @ConstructorProperties({"_id", "name", "scoreboard", "selectable_map", "min_players", "max_players", "permission", "group"})
    public CoreSubGamemode(@NotNull String id, @NotNull String name, @NotNull String scoreboard, boolean selectMap, int minPlayers, int maxPlayers, @NotNull String permission, @NotNull String serverGroup) {
        this.id = id;
        this.name = name;
        this.scoreboard = scoreboard;
        this.selectMap = selectMap;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.permission = permission;
        this.serverGroup = serverGroup;
    }

    @Override
    public String getId() {
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
    public boolean canSelectMap() {
        return this.selectMap;
    }

    @Override
    public int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public @NotNull String getPermission() {
        return this.permission;
    }

    @Override
    public @NotNull String getServerGroup() {
        return this.serverGroup;
    }
}
