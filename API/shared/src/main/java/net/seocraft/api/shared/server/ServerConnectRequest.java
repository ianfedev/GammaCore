package net.seocraft.api.shared.server;

import com.google.gson.Gson;
import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.QueryBuilder;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.Server;
import org.apache.http.client.ResponseHandler;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerConnectRequest extends HttpRequest {

    private HashMap<String, String> params = new HashMap<>();
    private String body;
    private Gson gson;

    @Inject public ServerConnectRequest(Gson gson) {
        this.gson = gson;
    }

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

    public String executeRequest(Server server, @Nullable String map) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        if (map != null) this.params.put("request", map);
        body = gson.toJson(server, Server.class);
        return getResponse();
    }
}
