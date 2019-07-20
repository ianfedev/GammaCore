package net.seocraft.commons.core.backend.user;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;

import java.util.HashMap;
import java.util.Map;

public class UserLoginRequest extends HttpRequest {

    @Inject JsonObject jsonObject;
    @Inject JsonUtils parser;
    private HashMap<String, String> headers = new HashMap<>();

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.POST;
    }

    @Override
    public String getJSONParams() {
        return this.parser.encode(this.jsonObject);
    }

    public String getURL() {
        return "user/login-server";
    }

    public String executeRequest(String username, String password, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.jsonObject.addProperty("username", username);
        this.jsonObject.addProperty("password", password);
        headers.put("authorization", token);
        return getResponse();
    }
}
