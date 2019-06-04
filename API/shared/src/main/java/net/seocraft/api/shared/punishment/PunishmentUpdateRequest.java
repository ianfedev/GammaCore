package net.seocraft.api.shared.punishment;

import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PunishmentUpdateRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private String punishmentId;
    private String body;

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.PUT;
    }

    public String getURL() {
        return "punishment/update/" +  this.punishmentId;
    }

    @Override
    public String getJSONParams() {
        return body;
    }

    public String executeRequest(@Nullable String punishment, String punishmentId, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.body = punishment;
        this.punishmentId = punishmentId;
        this.headers.put("authorization", token);
        return getResponse();
    }
}
