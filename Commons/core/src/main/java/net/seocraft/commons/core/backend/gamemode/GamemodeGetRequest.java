package net.seocraft.commons.core.backend.gamemode;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import org.jetbrains.annotations.NotNull;

public class GamemodeGetRequest extends HttpRequest {

    private String id;

    @Override
    public HttpType getType() {
        return HttpType.GET;
    }

    @Override
    public String getURL() {
        return "gamemode/get/" + this.id;
    }

    public String executeRequest(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.id = id;
        return getResponse();
    }

}
