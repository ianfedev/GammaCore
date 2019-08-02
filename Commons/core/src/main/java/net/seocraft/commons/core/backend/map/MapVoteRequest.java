package net.seocraft.commons.core.backend.map;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MapVoteRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String request;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public HttpType getType() {
        return HttpType.POST;
    }

    @Override
    public String getJSONParams() {
        return this.request;
    }

    @Override
    public String getURL() {
        return "map/vote";
    }

    public String executeRequest(@NotNull String request, @NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.request = request;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
