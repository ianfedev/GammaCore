package net.seocraft.commons.core.backend.friend;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FriendClearRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String id;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public HttpType getType() {
        return HttpType.DELETE;
    }

    @Override
    public String getURL() {
        return "friend/remove-all/" + this.id;
    }

    public String executeRequest(@NotNull String id, @NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.id = id;
        this.headers.put("authorization", token);
        return getResponse();
    }

}