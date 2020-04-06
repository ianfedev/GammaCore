package net.seocraft.api.core.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface UserStorageProvider {

    /**
     * Store user at cache provider
     * @param user object which will be stored
     */
    void cacheStoreUser(@NotNull User user) throws JsonProcessingException;

    /**
     * Retrieves cached user or create cache again if invalidated
     * @param id of the user
     * @return Async callback of user
     */
    @NotNull ListenableFuture<AsyncResponse<User>> getCachedUser(@NotNull String id);

    /**
     * Retrieve cached user or create cache again if invalidated
     * @param id of the user
     * @return user model of cached user
     * @throws Unauthorized Thrown when no authentication Token.
     * @throws BadRequest Thrown when the request was not sent correctly.
     * @throws NotFound Thrown when the user getId was not found.
     * @throws InternalServerError Thrown when the server had an internal error.
     */
    @NotNull User getCachedUserSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    /**
     * Retrieve user data from server.
     * @param username registered at the database
     * @return Async callback of user
     */
    @NotNull ListenableFuture<AsyncResponse<User>> findUserByName(@NotNull String username);

    /**
     * Retrieve user data from server.
     * @param username registered at the database
     * @return user model of queried username
     * @throws Unauthorized Thrown when no authentication Token.
     * @throws BadRequest Thrown when the request was not sent correctly.
     * @throws NotFound Thrown when the user getId was not found.
     * @throws InternalServerError Thrown when the server had an internal error.
     */
    @NotNull User findUserByNameSync(@NotNull String username) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    /**
     * Retrieve user data from server.
     * @param id registered at the database
     * @return user model of queried username
     * @throws Unauthorized Thrown when no authentication Token.
     * @throws BadRequest Thrown when the request was not sent correctly.
     * @throws NotFound Thrown when the user getId was not found.
     * @throws InternalServerError Thrown when the server had an internal error.
     */
    @NotNull User findUserByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    /**
     * @see Model
     * Updates database user.
     * @param user designed as Model with an ID to update
     * @return updated user.
     * @throws Unauthorized Thrown when no authentication Token.
     * @throws BadRequest Thrown when the request was not sent correctly.
     * @throws NotFound Thrown when the user getId was not found.
     * @throws InternalServerError Thrown when the server had an internal error.
     */
    @NotNull User updateUser(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError, JsonProcessingException;

    void invalidateUserCache(@NotNull User user);

}