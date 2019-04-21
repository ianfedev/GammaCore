package net.seocraft.api.shared.user;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.serialization.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class UserAccessRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    @Inject private JsonUtils parser;
    private String body;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.POST;
    }

    public String getURL() {
        return "user/access";
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public String executeRequest(JsonObject request, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = this.parser.encode(request);
        this.headers.put("authorization", token);
        return getResponse();
    }

}
