package net.seocraft.api.bukkit.creator.skin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.creator.concurrent.AsyncResponse;
import net.seocraft.api.bukkit.creator.concurrent.SimpleAsyncResponse;
import net.seocraft.api.bukkit.creator.concurrent.WrappedResponse;
import net.seocraft.api.bukkit.creator.skin.requests.MojangUUIDRequest;
import net.seocraft.api.bukkit.creator.skin.requests.SkinPropertiesRequest;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

public class CraftSkinHandler implements SkinHandler {

    @Inject private ListeningExecutorService executorService;
    @Inject private ObjectMapper mapper;
    @Inject private MojangUUIDRequest mojangUUIDRequest;
    @Inject private SkinPropertiesRequest skinPropertiesRequest;

    @Override
    public @NotNull AsyncResponse<SkinProperty> getSkinProperties(@NotNull String name) {
        return new SimpleAsyncResponse<>(this.executorService.submit(() -> {
            try {
                return new WrappedResponse<>(WrappedResponse.Status.SUCCESS, this.getSkinPropertiesSync(name), null);
            } catch (NotFound exception) {
                return new WrappedResponse<>(WrappedResponse.Status.ERROR, null, exception);
            }
        }));
    }

    @Override
    public @NotNull SkinProperty getSkinPropertiesSync(@NotNull String name) throws NotFound, IOException, URISyntaxException, Unauthorized, InternalServerError, BadRequest {
        String rawRequest = this.mojangUUIDRequest.executeRequest(name);
        if (rawRequest == null) throw new NotFound("User not found in mojang API");
        String uuid = this.mapper.readTree(rawRequest).get("id").asText();
        JsonNode node = this.mapper.readTree(this.skinPropertiesRequest.executeRequest(uuid));
        SkinProperty[] skinProperties = mapper.readValue(node.get("raw").get("properties").toString(), SkinProperty[].class);
        return skinProperties[0];
    }
}