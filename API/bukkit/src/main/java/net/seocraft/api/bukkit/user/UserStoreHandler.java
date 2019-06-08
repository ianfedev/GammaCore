package net.seocraft.api.bukkit.user;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UserStoreHandler {

    void cacheStoreUser(@NotNull User user);

    @NotNull ListenableFuture<AsyncResponse<User>> getCachedUser(@NotNull String id);

    @NotNull User getCachedUserSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @Nullable ListenableFuture<AsyncResponse<User>> findUserByName(@NotNull String username);

    @Nullable User findUserByNameSync(@NotNull String username) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull User findUserByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull User updateUser(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError;

}