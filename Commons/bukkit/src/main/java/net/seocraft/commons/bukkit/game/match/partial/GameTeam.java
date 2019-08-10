package net.seocraft.commons.bukkit.game.match.partial;

import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.bukkit.game.match.partial.TeamMember;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

public class GameTeam implements Team {

    @NotNull private String name;
    @NotNull private Set<TeamMember> members;
    @NotNull private String color;

    @ConstructorProperties({"name", "members", "color"})
    public GameTeam(@NotNull String name, @NotNull Set<TeamMember> members, @NotNull String color) {
        this.name = name;
        this.members = members;
        this.color = color;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull Set<TeamMember> getMembers() {
        return this.members;
    }

    @Override
    public @NotNull String getColor() {
        return this.color;
    }

    @Override
    public @NotNull ChatColor getChatColor() {
        return ChatColor.valueOf(this.color);
    }
}
