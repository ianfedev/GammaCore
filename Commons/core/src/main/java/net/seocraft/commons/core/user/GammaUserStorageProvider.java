package net.seocraft.commons.core.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.commons.core.backend.user.UserGetRequest;
import net.seocraft.commons.core.backend.user.UserUpdateRequest;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class GammaUserStorageProvider implements UserStorageProvider {

    @Inject private UserGetRequest userGetRequest;
    @Inject private UserUpdateRequest userUpdateRequest;
    @Inject private ListeningExecutorService executorService;
    @Inject private ObjectMapper objectMapper;
    @Inject private ServerTokenQuery tokenHandler;
    @Inject private RedisClient client;

    @Override
    public void cacheStoreUser(@NotNull User user) throws JsonProcessingException {
        String idString = user.id();
        this.client.setString("user:" + idString, objectMapper.writeValueAsString(user));
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
    public @NotNull User getCachedUserSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        if (this.client.existsKey("user:" + id)) {
            return this.objectMapper.readValue(this.client.getString("user:" + id), User.class);
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
    public @Nullable User findUserByNameSync(@NotNull String username) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        return this.objectMapper.readValue(
                this.userGetRequest.executeRequest(username, this.tokenHandler.getToken()),
                User.class
        );
    }

    @Override
    public @NotNull User findUserByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        User deserializeUser = this.objectMapper.readValue(
                this.userGetRequest.executeRequest(id, this.tokenHandler.getToken()),
                User.class
        );
        cacheStoreUser(deserializeUser);
        return deserializeUser;
    }

    @Override
    public @NotNull User updateUser(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError, JsonProcessingException {
        this.userUpdateRequest.executeRequest(user.id(), this.objectMapper.writeValueAsString(user), this.tokenHandler.getToken());
        cacheStoreUser(user);
        return user;
    }

}
