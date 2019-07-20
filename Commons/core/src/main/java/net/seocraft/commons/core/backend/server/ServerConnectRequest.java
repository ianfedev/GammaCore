package net.seocraft.commons.core.backend.server;

import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import java.util.HashMap;
import java.util.Map;

public class ServerConnectRequest extends HttpRequest {

    private HashMap<String, String> params = new HashMap<>();
    private String body;

    @Override
    public Map<String, String> getQueryStrings() {
        return this.params;
    }

    public HttpType getType() {
        return HttpType.POST;
    }

    public String getURL() {
        return "server/connect";
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public String executeRequest(String server) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = server;
        return getResponse();
    }
}
