package net.seocraft.commons.core.backend.punishment;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PunishmentListRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String body;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public HttpType getType() {
        return HttpType.POST;
    }

    public String getURL() {
        return "punishment/list";
    }

    public String executeRequest(@Nullable String body, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = body;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
