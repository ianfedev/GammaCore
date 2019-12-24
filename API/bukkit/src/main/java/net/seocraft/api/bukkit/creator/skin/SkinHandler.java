package net.seocraft.api.bukkit.creator.skin;

import net.seocraft.api.bukkit.creator.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

public interface SkinHandler {

    @NotNull AsyncResponse<SkinProperty> getSkinProperties(@NotNull String name);

    @NotNull SkinProperty getSkinPropertiesSync(@NotNull String name) throws NotFound, InternalServerError, IOException, URISyntaxException, Unauthorized, BadRequest;

}