package net.seocraft.commons.core.backend.punishment;

import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;

import java.util.HashMap;
import java.util.Map;

public class PunishmentGetRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String punishmentId;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.GET;
    }

    public String getURL() {
        return "punishment/get-model/" + this.punishmentId;
    }

    public String executeRequest(String punishmentId, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.punishmentId = punishmentId;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
