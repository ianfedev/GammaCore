package net.seocraft.commons.core.backend.user;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;

import java.util.HashMap;
import java.util.Map;

public class UserDisconnectRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String body;
    private String id;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.GET;
    }

    public String getURL() {
        return "session/user-disconnect/" + this.id;
    }

    public String executeRequest(String id, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.id = id;
        this.headers.put("authorization", token);
        return getEpsilonResponse();
    }

}
