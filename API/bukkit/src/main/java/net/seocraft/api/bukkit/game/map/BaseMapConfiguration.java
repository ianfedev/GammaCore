package net.seocraft.api.bukkit.game.map;

import net.seocraft.api.bukkit.game.map.partial.Contribution;
import net.seocraft.api.bukkit.game.map.partial.MapCoordinates;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface BaseMapConfiguration {

    @NotNull String getName();

    @NotNull String getAuthor();

    @NotNull String getVersion();

    @NotNull Set<Contribution> getContributors();

    @NotNull String getGamemode();

    @NotNull String getSubGamemode();

    @NotNull String getDescription();

    @NotNull MapCoordinates getLobbyCoordinates();

    @NotNull MapCoordinates getSpectatorSpawn();

}
