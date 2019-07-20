package net.seocraft.commons.bukkit.old.friend;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.commons.bukkit.server.ServerTokenQuery;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.core.backend.friend.*;
import net.seocraft.commons.core.backend.http.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Singleton
public class FriendshipHandlerImpl implements FriendshipHandler {

    private ListeningExecutorService executorService;
    private Gson gson;
    private Channel<Friendship> friendshipChannel;
    @Inject private JsonUtils jsonParser;
    @Inject private FriendshipUserActions friendshipUserActions;
    @Inject private FriendCreateRequest friendCreateRequest;
    @Inject private FriendCheckRequest friendCheckRequest;
    @Inject private FriendListRequest friendListRequest;
    @Inject private RedisClient client;
    @Inject private FriendDeleteRequest friendDeleteRequest;
    @Inject private FriendClearRequest friendClearRequest;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private ServerTokenQuery serverTokenQuery;

    @Inject FriendshipHandlerImpl(ListeningExecutorService executorService, Messager messager, Gson gson) {
        this.executorService = executorService;
        this.gson = gson;
        this.friendshipChannel = messager.getChannel("friendships", Friendship.class);
        this.friendshipChannel.registerListener(new FriendshipListener(this.userStorageProvider, this.friendshipUserActions));
    }

    @Override
    public void createFriendRequest(@NotNull String sender, @NotNull String receiver) {
        if (this.client.existsKey("friendship:" + receiver + ":" + sender)) return;
        Friendship friendship = new FriendshipImpl(sender, receiver, FriendshipAction.CREATE, false, null);
        this.client.setString("friendship:" + receiver + ":" + sender,
                this.gson.toJson(friendship)
        );
        this.client.setExpiration("friendship:" + receiver + ":" + sender, 600);
        this.friendshipChannel.sendMessage(friendship);
    }

    @Override
    public void acceptFriendRequest(@NotNull String sender, @NotNull String receiver) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        if (!this.client.existsKey("friendship:" + sender + ":" + receiver)) return;
        this.client.deleteString("friendship:" + sender + ":" + receiver);
        Friendship punishment = new FriendshipImpl(sender, receiver, FriendshipAction.ACCEPT, false, null);
        this.friendCreateRequest.executeRequest(
                this.gson.toJson(punishment),
                this.serverTokenQuery.getToken()
        );
        this.friendshipChannel.sendMessage(punishment);
    }

    @Override
    public boolean checkFriendshipStatus(@NotNull String sender, @NotNull String receiver) {
        try {
            return this.jsonParser.parseJson(
                    this.friendCheckRequest.executeRequest(sender, receiver, this.serverTokenQuery.getToken()),
                    "status"
            ).getAsBoolean();
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError ignore) { return false; }
    }

    @Override
    public void rejectFriendRequest(@NotNull String sender, @NotNull String receiver) {
        if (!this.client.existsKey("friendship:" + sender + ":" + receiver)) return;
        this.client.deleteString("friendship:" + sender + ":" + receiver);
    }

    @Override
    public void removeFriend(@NotNull String sender, @NotNull String receiver) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.friendDeleteRequest.executeRequest(sender, receiver, this.serverTokenQuery.getToken());
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<List<User>>> listFriends(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, listFriendsSync(id));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @Nullable List<User> listFriendsSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        List<User> userList = new ArrayList<>();
        List<Friendship> userFriendships = this.gson.fromJson(
                this.friendListRequest.executeRequest(id, this.serverTokenQuery.getToken()),
                new TypeToken<List<FriendshipImpl>>(){}.getType()
        );
        userFriendships.forEach(friendship -> {
            try {
                if (friendship.getSender().equalsIgnoreCase(id)) {
                    userList.add(this.userStorageProvider.findUserByIdSync(friendship.getReceiver()));
                } else {
                    userList.add(this.userStorageProvider.findUserByIdSync(friendship.getSender()));
                }
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError ignore) {}
        });
        return userList;
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<List<User>>> getRequests(@NotNull String id) {
        return this.executorService.submit(() -> new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getRequestsSync(id)));
    }

    @Override
    public @Nullable List<User> getRequestsSync(@NotNull String id) {
        Set<String> requestList = this.client.getKeys("friendship:" + id +"*");
        List<User> userList = new ArrayList<>();
        requestList.forEach(key -> {
            Friendship friendshipRecord = this.gson.fromJson(key, FriendshipImpl.class);
            long expirationTime = this.client.getExpiringTime(key);
            friendshipRecord.setAlerted(true);
            this.client.setString(key, this.gson.toJson(friendshipRecord));
            this.client.setExpiration(key, expirationTime);
            if (!friendshipRecord.isAlerted())
            try {
                userList.add(
                        this.userStorageProvider.findUserByIdSync(friendshipRecord.getSender())
                );
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError ignore) {}
        });
        return userList;
    }

    @Override
    public boolean hasUnreadRequests(@NotNull String id) {
        Set<String> requestList = this.client.getKeys("friendship:" + id +"*");
        for (String friendship: requestList) {
            Friendship friendshipRecord = this.gson.fromJson(
                    this.client.getString(friendship),
                    FriendshipImpl.class);
            if (!friendshipRecord.isAlerted()) return true;
        }
        return false;
    }

    @Override
    public boolean requestIsSent(@NotNull String sender, @NotNull String receiver) {
        return this.client.existsKey("friendship:" + receiver + ":" + sender);
    }

    @Override
    public void toggleFriendRequests(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        user.setAcceptingFriends(!user.isAcceptingFriends());
        this.userStorageProvider.updateUser(user);
    }

    @Override
    public void removeAllFriends(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.friendClearRequest.executeRequest(id, this.serverTokenQuery.getToken());
    }

    @Override
    public void forceFriend(@NotNull String firstId, @Nullable String secondId, @NotNull String issuer) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        Friendship punishment = new FriendshipImpl(firstId, secondId, FriendshipAction.FORCE, false, issuer);
        this.friendCreateRequest.executeRequest(
                this.gson.toJson(punishment),
                this.serverTokenQuery.getToken()
        );
        this.friendshipChannel.sendMessage(punishment);
    }
}
