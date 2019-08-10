package net.seocraft.api.bukkit.game.match.partial;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Team {

    @NotNull String getName();

    @NotNull Set<TeamMember> getMembers();

    @NotNull String getColor();

    @JsonIgnore
    @NotNull ChatColor getChatColor();
}
