package net.seocraft.api.bukkit.game.map;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.seocraft.api.bukkit.game.map.partial.Contribution;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

public interface MapProvider {

    @NotNull GameMap loadMapSync(
            @NotNull String name,
            @NotNull String file,
            @NotNull String configuration,
            @NotNull String image,
            @NotNull String author,
            @NotNull String version,
            @NotNull Set<Contribution> contributors,
            @NotNull String gamemode,
            @NotNull String subGamemode,
            @NotNull String description
    ) throws InternalServerError, IOException, Unauthorized, NotFound, BadRequest;
}
