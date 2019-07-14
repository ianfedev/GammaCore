package net.seocraft.api.shared.gamemode;

import net.seocraft.api.shared.http.HttpRequest;
import net.seocraft.api.shared.http.HttpType;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GamemodeGetRequest extends HttpRequest {

    private String id;

    @Override
    public HttpType getType() {
        return HttpType.GET;
    }

    @Override
    public String getURL() {
        return "gamemode/get/" + this.id;
    }

    public String executeRequest(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.id = id;
        return getResponse();
    }

}
