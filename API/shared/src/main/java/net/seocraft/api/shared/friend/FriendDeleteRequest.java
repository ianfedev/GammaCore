package net.seocraft.api.shared.friend;

import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FriendDeleteRequest extends HttpRequest {

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

    @Override
    public HttpType getType() {
        return HttpType.DELETE;
    }

    @Override
    public String getURL() {
        return "friend/delete";
    }

    public String executeRequest(@NotNull String sender, @NotNull String receiver, @NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.params.put("sender", sender);
        this.params.put("receiver", receiver);
        this.headers.put("authorization", token);
        return getResponse();
    }

}