package net.seocraft.api.bukkit.creator.skin.requests;

import com.google.inject.Singleton;
import net.seocraft.api.bukkit.creator.http.AbstractHttpRequest;
import net.seocraft.api.bukkit.creator.http.HttpType;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Singleton
public class MojangUUIDRequest extends AbstractHttpRequest {

    @Override
    public @NotNull HttpType getType() {
        return HttpType.GET;
    }

    public String executeRequest(String username) throws Unauthorized, BadRequest, NotFound, InternalServerError, MalformedURLException, URISyntaxException {
        return request("https://api.mojang.com/users/profiles/minecraft/" + username);
    }
}