package net.seocraft.api.bukkit.creator.http;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface HttpRequest {

    @NotNull HttpType getType();

    @NotNull Map<String, String> getHeaders();

    @NotNull String getJSONParams();

}