package net.seocraft.api.shared.user;

import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;

import java.util.HashMap;
import java.util.Map;

public class UserDataRequest extends HttpRequest {

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
