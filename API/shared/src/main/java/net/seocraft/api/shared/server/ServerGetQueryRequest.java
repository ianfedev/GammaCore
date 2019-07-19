package net.seocraft.api.shared.server;

import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;

import java.util.HashMap;
import java.util.Map;

public class ServerGetQueryRequest extends HttpRequest {

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
        return "server/get-query";
    }

    @Override
    public String getJSONParams() {
        return this.body;
    }

    public String executeRequest(String query, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = query;
        this.headers.put("authorization", token);
        return getResponse();
    }
}