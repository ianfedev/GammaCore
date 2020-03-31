package net.seocraft.commons.core.backend.user;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;

import java.util.HashMap;
import java.util.Map;

public class UserLoginRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String body;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.POST;
    }

    @Override
    public String getJSONParams() {
        return this.body;
    }

    public String getURL() {
        return "authentication/login-server";
    }

    public String executeRequest(String query, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = query;
        headers.put("authorization", token);
        return getEpsilonResponse();
    }
}
