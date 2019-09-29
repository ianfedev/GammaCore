package net.seocraft.commons.core.backend.stats;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;

import java.util.HashMap;
import java.util.Map;

public class StatsUpdateRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String body;
    private String id;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.PUT;
    }

    public String getURL() {
        return "stats/put/" + this.id;
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public String executeRequest(String id, String body, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.id = id;
        this.body = body;
        this.headers.put("authorization", token);
        return getResponse();
    }
}
