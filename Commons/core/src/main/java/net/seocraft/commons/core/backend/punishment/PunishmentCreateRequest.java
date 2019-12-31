package net.seocraft.commons.core.backend.punishment;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;

import java.util.HashMap;
import java.util.Map;

public class PunishmentCreateRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String body;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.POST;
    }

    public String getURL() {
        return "punishment/create";
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public String executeRequest(String request, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = request;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
