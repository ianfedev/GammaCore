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

public class PunishmentListRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> params = new HashMap<>();

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Map<String, String> getQueryStrings() {
        return this.params;
    }

    public HttpType getType() {
        return HttpType.GET;
    }

    public String getURL() {
        return "punishment/list-model";
    }

    public String executeRequest(@Nullable String type, @Nullable String playerId, boolean active, String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.params.put("type", type);
        this.params.put("id", playerId);
        this.params.put("active", Boolean.toString(active));
        this.headers.put("authorization", token);
        return getResponse();
    }

}
