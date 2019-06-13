package net.seocraft.commons.bukkit.friend;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FriendshipHandler {

    void createFriendRequest(@NotNull String sender, @NotNull String receiver);

    void acceptFriendRequest(@NotNull String sender, @NotNull String receiver) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    boolean checkFriendshipStatus(@NotNull String sender, @NotNull String receiver);

    void rejectFriendRequest(@NotNull String sender, @NotNull String receiver);

    void removeFriend(@NotNull String sender, @NotNull String receiver) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<List<User>>> listFriends(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @Nullable List<User> listFriendsSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<List<User>>> getRequests(@NotNull String id);

    @Nullable List<User> getRequestsSync(@NotNull String id);

    boolean requestIsSent(@NotNull String sender, @NotNull String receiver);

    void toggleFriendRequests(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    void removeAllFriends(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

    void forceFriend(@NotNull String firstId, @Nullable String secondId) throws Unauthorized, BadRequest, NotFound, InternalServerError;

}
