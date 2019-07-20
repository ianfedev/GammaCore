package net.seocraft.commons.core.backend.gamemode;

import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;

public class GamemodeListRequest extends HttpRequest {

    @Override
    public HttpType getType() {
        return HttpType.GET;
    }

    @Override
    public String getURL() {
        return "gamemode/list";
    }

    public String executeRequest() throws Unauthorized, BadRequest, NotFound, InternalServerError {
        return getResponse();
    }

}
