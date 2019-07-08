package net.seocraft.api.shared.server;

import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ServerDisconnectRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.DELETE;
    }

    public String getURL() {
        return "server/disconnect";
    }

    public String executeRequest(@NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.headers.put("authorization", token);
        return getResponse();
    }
}
