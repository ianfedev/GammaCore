package net.seocraft.api.shared.punishment;

import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PunishmentGetLastRequest extends HttpRequest {

    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> params = new HashMap<>();

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public HttpType getType() {
        return HttpType.GET;
    }

    @Override
    public Map<String, String> getQueryStrings() {
        return this.params;
    }

    public String getURL() {
        return "punishment/get-last";
    }

    public String executeRequest(@Nullable String type, @NotNull String punishmentId, @NotNull String token) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.params.put("id", punishmentId);
        this.params.put("type", type);
        this.headers.put("authorization", token);
        return getResponse();
    }

}
