package net.seocraft.api.bukkit.creator.concurrent;

import org.jetbrains.annotations.NotNull;

public interface AsyncResponse<T> {

    void callback(@NotNull Callback<Response<T>> callback);

}