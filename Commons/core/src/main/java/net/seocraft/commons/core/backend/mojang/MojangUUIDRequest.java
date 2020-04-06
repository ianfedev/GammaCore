package net.seocraft.commons.core.backend.mojang;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;

import java.util.HashMap;
import java.util.Map;

public class MojangUUIDRequest extends HttpRequest {

    private String uuid;
    public HttpType getType() {
        return HttpType.POST;
    }

    public String getURL() {
        return "uuid/" + this.uuid;
    }

    public String executeRequest(String uuid) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.uuid = uuid;
        return getCustomResponse("https://api.minetools.eu");
    }

}
