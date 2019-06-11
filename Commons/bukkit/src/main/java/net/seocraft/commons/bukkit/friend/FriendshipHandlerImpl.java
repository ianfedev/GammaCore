package net.seocraft.commons.bukkit.friend;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.redis.Channel;
import net.seocraft.api.shared.redis.Messager;
import net.seocraft.api.shared.redis.RedisClient;
import net.seocraft.api.shared.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Singleton
public class FriendshipHandlerImpl implements FriendshipHandler {

    private ListeningExecutorService executorService;
    private Gson gson;
    private Channel<Friendship> friendshipChannel;
    private RedisClient client;

    @Inject FriendshipHandlerImpl(ListeningExecutorService executorService, Messager messager, RedisClient client, Gson gson) {
        this.executorService = executorService;
        this.client = client;
        this.gson = gson;
        this.friendshipChannel = messager.getChannel("friendships", Friendship.class);
        this.friendshipChannel.registerListener(new FriendshipListener());
    }

    @Override
    public void createFriendRequest(@NotNull String sender, @NotNull String receiver) {
        if (this.client.existsKey("friendship:" + sender + ":" + receiver)) return;
        Friendship friendship = new FriendshipImpl(sender, receiver, FriendshipAction.CREATE);
        this.client.setString("friendship:" + sender + ":" + receiver,
                this.gson.toJson(friendship)
        );
        this.friendshipChannel.sendMessage(friendship);
    }

    @Override
    public void acceptFriendRequest(@NotNull String sender, @NotNull String receiver) {
        if (!this.client.existsKey("friendship:" + sender + ":" + receiver)) return;
        this.client.deleteString("friendship:" + sender + ":" + receiver);
        // TODO: Create action
        this.friendshipChannel.sendMessage(new FriendshipImpl(sender, receiver, FriendshipAction.ACCEPT));
    }

    @Override
    public boolean checkFriendshipStatus(@NotNull String sender, @NotNull String receiver) {
        return false;
    }

    @Override
    public void rejectFriendRequest(@NotNull String sender, @NotNull String receiver) {

    }

    @Override
    public void removeFriend(@NotNull String sender, @NotNull String receiver) {

    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<List<User>>> listFriends() {
        return null;
    }

    @Override
    public @Nullable List<User> listFriendsSync() {
        return null;
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<List<User>>> getRequests() {
        return null;
    }

    @Override
    public @Nullable List<User> getRequestsSync() {
        return null;
    }

    @Override
    public void toggleFriendRequests() {

    }

    @Override
    public void removeAllFriends() {

    }

    @Override
    public void forceFriend(@NotNull String firstId, @Nullable String secondId) {

    }
}
