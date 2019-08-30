package net.seocraft.api.core.friend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public interface FriendshipProvider {

    void createFriendRequest(@NotNull String sender, @NotNull String receiver) throws JsonProcessingException;

    void acceptFriendRequest(@NotNull String sender, @NotNull String receiver) throws Unauthorized, BadRequest, NotFound, InternalServerError, JsonProcessingException;

    boolean checkFriendshipStatus(@NotNull String sender, @NotNull String receiver);

    void rejectFriendRequest(@NotNull String sender, @NotNull String receiver);

    void removeFriend(@NotNull String sender, @NotNull String receiver) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<Set<User>>> listFriends(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull Set<User> listFriendsSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull ListenableFuture<AsyncResponse<Set<User>>> getRequests(@NotNull String id);

    @Nullable Set<User> getRequestsSync(@NotNull String id);

    boolean hasUnreadRequests(@NotNull String id) throws IOException;

    boolean requestIsSent(@NotNull String sender, @NotNull String receiver);

    void toggleFriendRequests(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError, JsonProcessingException;

    void removeAllFriends(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    void forceFriend(@NotNull String firstId, @Nullable String secondId, @NotNull String issuer) throws Unauthorized, BadRequest, NotFound, InternalServerError, JsonProcessingException;

}
