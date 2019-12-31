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

public class FriendCheckRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> params = new HashMap<>();

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Map<String, String> getQueryStrings() {
        return this.params;
    }

    public HttpType getType() {
        return HttpType.GET;
    }

    public String getURL() {
        return "friend/check";
    }

    public String executeRequest(@NotNull String sender, @NotNull String receiver, @NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.params.put("sender", sender);
        this.params.put("receiver", receiver);
        this.headers.put("authorization", token);
        return getResponse();
    }

}