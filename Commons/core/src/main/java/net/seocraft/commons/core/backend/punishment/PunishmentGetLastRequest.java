package net.seocraft.commons.core.backend.punishment;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.backend.http.HttpRequest;
import net.seocraft.commons.core.backend.http.HttpType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PunishmentGetLastRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String body;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.POST;
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public String getURL() {
        return "punishment/get-last";
    }

    public String executeRequest(@NotNull String body, @NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = body;
        this.headers.put("authorization", token);
        return getResponse();
    }

}
