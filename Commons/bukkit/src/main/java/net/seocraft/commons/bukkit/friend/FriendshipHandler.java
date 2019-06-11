package net.seocraft.commons.bukkit.friend;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FriendshipHandler {

    void createFriendRequest(@NotNull String sender, @NotNull String receiver);

    void acceptFriendRequest(@NotNull String sender, @NotNull String receiver);

    boolean checkFriendshipStatus(@NotNull String sender, @NotNull String receiver);

    void rejectFriendRequest(@NotNull String sender, @NotNull String receiver);

    void removeFriend(@NotNull String sender, @NotNull String receiver);

    @NotNull ListenableFuture<AsyncResponse<List<User>>> listFriends();

    @Nullable List<User> listFriendsSync();

    @NotNull ListenableFuture<AsyncResponse<List<User>>> getRequests();

    @Nullable List<User> getRequestsSync();

    void toggleFriendRequests();

    void removeAllFriends();

    void forceFriend(@NotNull String firstId, @Nullable String secondId);

}
