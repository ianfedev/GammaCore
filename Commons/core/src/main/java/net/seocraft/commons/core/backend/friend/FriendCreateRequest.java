package net.seocraft.commons.core.backend.friend;

import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FriendCreateRequest extends HttpRequest {

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
        return "friend/create";
    }

    public String executeRequest(@NotNull String body, @NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = body;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
