package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface MatchFinder {

    @NotNull FinderResult findAvailableMatch(@NotNull String gamemode, @NotNull String subGamemode, @NotNull String serverGroup) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException;
}
