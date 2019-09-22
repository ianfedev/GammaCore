package net.seocraft.commons.core.backend.match;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MatchUpdateRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String match;
    private String id;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public HttpType getType() {
        return HttpType.PUT;
    }

    @Override
    public String getJSONParams() {
        return this.match;
    }

    @Override
    public String getURL() {
        return "match/update/" + this.id;
    }

    public String executeRequest(@NotNull String match, @NotNull String id, @NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.match = match;
        this.id = id;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
