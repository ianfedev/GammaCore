package net.seocraft.api.bukkit.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface StatsProvider {

    @NotNull Stats getPlayerStats(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull Stats updatePlayerStats(@NotNull Stats stats) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError;

}
