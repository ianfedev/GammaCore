package net.seocraft.api.shared.punishment;

import com.google.inject.Inject;
import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.serialization.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class PunishmentGetRequest extends HttpRequest {
    private HashMap<String, String> headers = new HashMap<>();
    private String punishmentId;
    @Inject private JsonUtils parser;
    private String body;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.GET;
    }

    public String getURL() {
        return "punishment/get-model/" + this.punishmentId;
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public String executeRequest(String punishmentId, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.punishmentId = punishmentId;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
