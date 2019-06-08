package net.seocraft.api.shared.user;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.model.User;

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
        this.body = this.gson.toJson(user, User.class);
        return getResponse();
    }
}
