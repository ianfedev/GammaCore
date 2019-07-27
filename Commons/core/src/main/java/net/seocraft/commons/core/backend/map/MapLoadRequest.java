package net.seocraft.commons.core.backend.map;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import org.jetbrains.annotations.NotNull;

public class MapLoadRequest extends HttpRequest {

    private String map;

    @Override
    public HttpType getType() {
        return HttpType.POST;
    }

    @Override
    public String getJSONParams() {
        return this.map;
    }

    @Override
    public String getURL() {
        return "map/load";
    }

    public String executeRequest(@NotNull String map) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.map = map;
        return getResponse();
    }

}
