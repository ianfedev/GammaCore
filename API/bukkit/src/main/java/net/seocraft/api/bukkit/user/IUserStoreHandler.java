package net.seocraft.api.bukkit.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.redis.RedisClient;
import net.seocraft.api.shared.serialization.model.UserDeserializer;
import net.seocraft.api.shared.user.UserGetRequest;
import net.seocraft.api.shared.user.UserUpdateRequest;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.api.shared.user.model.UserImp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IUserStoreHandler implements UserStoreHandler {

    @Inject private UserGetRequest userGetRequest;
    @Inject private UserUpdateRequest userUpdateRequest;
    @Inject private ListeningExecutorService executorService;
    @Inject private UserDeserializer userDeserializer;
    @Inject private ServerTokenQuery tokenHandler;
    @Inject private Gson gson;
    @Inject private RedisClient client;

    @Override
    public void cacheStoreUser(@NotNull User user) {
        String idString = user.id();
        this.client.setString("user:" + idString, gson.toJson(user));
        this.client.setExpiration("user:" + idString, 120);
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<User>> getCachedUser(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, this.getCachedUserSync(id));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull User getCachedUserSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        if (this.client.existsKey("user:" + id)) {
            return this.gson.fromJson(this.client.getString("user:" + id), UserImp.class);
        } else {
            return findUserByIdSync(id);
        }
    }

    @Override
    public @Nullable ListenableFuture<AsyncResponse<User>> findUserByName(@NotNull String username) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, this.findUserByNameSync(username));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @Nullable User findUserByNameSync(@NotNull String username) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        return this.userDeserializer.deserializeModel(
                this.userGetRequest.executeRequest(username, this.tokenHandler.getToken())
        );
    }

    @Override
    public @NotNull User findUserByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        User deserializeUser = this.userDeserializer.deserializeModel(
                this.userGetRequest.executeRequest(id, this.tokenHandler.getToken())
        );
        cacheStoreUser(deserializeUser);
        return deserializeUser;
    }

    @Override
    public @NotNull User updateUser(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.userUpdateRequest.executeRequest(user, this.tokenHandler.getToken());
        cacheStoreUser(user);
        return user;
    }

}
