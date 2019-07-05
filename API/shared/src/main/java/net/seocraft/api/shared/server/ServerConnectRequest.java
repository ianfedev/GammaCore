package net.seocraft.api.shared.server;

import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
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
