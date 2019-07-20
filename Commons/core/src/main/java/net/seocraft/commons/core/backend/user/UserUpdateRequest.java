package net.seocraft.commons.core.backend.user;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.core.backend.user.model.UserImp;

import java.util.HashMap;
import java.util.Map;

public class UserUpdateRequest extends HttpRequest {

    @Inject private Gson gson;
    private HashMap<String, String> headers = new HashMap<>();
    private String id;
    private String body;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.PUT;
    }

    public String getURL() {
        return "user/update-server/" + this.id;
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public String executeRequest(User user, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.headers.put("authorization", token);
        this.id = user.id();
        this.body = this.gson.toJson(user, UserImp.class);
        return getResponse();
    }
}
