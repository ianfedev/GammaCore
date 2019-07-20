package net.seocraft.commons.core.backend.user;

import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;

import java.util.HashMap;
import java.util.Map;

public class UserGetRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String username;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.GET;
    }

    public String getURL() {
        return "user/get/" + this.username;
    }

    public String executeRequest(String username, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.username = username;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
