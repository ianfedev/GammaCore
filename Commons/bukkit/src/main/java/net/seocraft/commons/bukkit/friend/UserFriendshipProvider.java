package net.seocraft.commons.bukkit.friend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.core.friend.Friendship;
import net.seocraft.api.core.friend.FriendshipAction;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.commons.bukkit.server.BukkitTokenQuery;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.core.backend.friend.*;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UserFriendshipProvider implements FriendshipProvider {

    private ListeningExecutorService executorService;
    private Channel<Friendship> friendshipChannel;
    @Inject private ObjectMapper mapper;
    @Inject private FriendCreateRequest friendCreateRequest;
    private UserStorageProvider userStorageProvider;
    @Inject private FriendCheckRequest friendCheckRequest;
    @Inject private FriendListRequest friendListRequest;
    @Inject private RedisClient client;
    @Inject private FriendDeleteRequest friendDeleteRequest;
    @Inject private FriendClearRequest friendClearRequest;
    @Inject private BukkitTokenQuery serverTokenQuery;

    @Inject UserFriendshipProvider(UserStorageProvider userStorageProvider, FriendshipUserActions friendshipUserActions, ListeningExecutorService executorService, Messager messager) {
        this.executorService = executorService;
        this.userStorageProvider = userStorageProvider;
        this.friendshipChannel = messager.getChannel("friendships", Friendship.class);
        this.friendshipChannel.registerListener(new FriendshipListener(userStorageProvider, friendshipUserActions));
    }

    @Override
    public void createFriendRequest(@NotNull String sender, @NotNull String receiver) throws JsonProcessingException {
        if (this.client.existsKey("friendship:" + receiver + ":" + sender)) return;
        Friendship friendship = new UserFriendship(sender, receiver, FriendshipAction.CREATE, false, null);
        this.client.setString("friendship:" + receiver + ":" + sender,
                this.mapper.writeValueAsString(friendship)
        );
        this.client.setExpiration("friendship:" + receiver + ":" + sender, 600);
        this.friendshipChannel.sendMessage(friendship);
    }

    @Override
    public void acceptFriendRequest(@NotNull String sender, @NotNull String receiver) throws Unauthorized, BadRequest, NotFound, InternalServerError, JsonProcessingException {
        if (!this.client.existsKey("friendship:" + sender + ":" + receiver)) return;
        this.client.deleteString("friendship:" + sender + ":" + receiver);
        Friendship friendship = new UserFriendship(sender, receiver, FriendshipAction.ACCEPT, false, null);
        this.friendCreateRequest.executeRequest(
                this.mapper.writeValueAsString(friendship),
                this.serverTokenQuery.getToken()
        );
        this.friendshipChannel.sendMessage(friendship);
    }

    @Override
    public boolean checkFriendshipStatus(@NotNull String sender, @NotNull String receiver) {
        try {
            return mapper.readTree(
                    this.friendCheckRequest.executeRequest(sender, receiver, this.serverTokenQuery.getToken())
            ).get("status").asBoolean();
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) { return false; }
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
    public @NotNull ListenableFuture<AsyncResponse<Set<User>>> listFriends(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, listFriendsSync(id));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @Nullable Set<User> listFriendsSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        Set<User> userList = new HashSet<>();
        Set<Friendship> userFriendships = this.mapper.readValue(
                this.friendListRequest.executeRequest(id, this.serverTokenQuery.getToken()),
                new TypeReference<Set<User>>(){}
        );
        userFriendships.forEach(friendship -> {
            try {
                if (friendship.getSender().equalsIgnoreCase(id)) {
                    userList.add(this.userStorageProvider.findUserByIdSync(friendship.getReceiver()));
                } else {
                    userList.add(this.userStorageProvider.findUserByIdSync(friendship.getSender()));
                }
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) {}
        });
        return userList;
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Set<User>>> getRequests(@NotNull String id) {
        return this.executorService.submit(() -> new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getRequestsSync(id)));
    }

    @Override
    public @Nullable Set<User> getRequestsSync(@NotNull String id) {
        Set<String> requestList = this.client.getKeys("friendship:" + id +"*");
        Set<User> userList = new HashSet<>();
        requestList.forEach(key -> {
            Friendship friendshipRecord = null;
            try {
                friendshipRecord = this.mapper.readValue(key, Friendship.class);
            } catch (IOException ignore) {}
            long expirationTime = this.client.getExpiringTime(key);
            friendshipRecord.setAlerted(true);
            try {
                this.client.setString(key, this.mapper.writeValueAsString(friendshipRecord));
            } catch (JsonProcessingException ignore) {}
            this.client.setExpiration(key, expirationTime);
            if (!friendshipRecord.isAlerted())
            try {
                userList.add(
                        this.userStorageProvider.findUserByIdSync(friendshipRecord.getSender())
                );
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) {}
        });
        return userList;
    }

    @Override
    public boolean hasUnreadRequests(@NotNull String id) throws IOException {
        Set<String> requestList = this.client.getKeys("friendship:" + id +"*");
        for (String friendship: requestList) {
            Friendship friendshipRecord = this.mapper.readValue(
                    this.client.getString(friendship),
                    Friendship.class);
            if (!friendshipRecord.isAlerted()) return true;
        }
        return false;
    }

    @Override
    public boolean requestIsSent(@NotNull String sender, @NotNull String receiver) {
        return this.client.existsKey("friendship:" + receiver + ":" + sender);
    }

    @Override
    public void toggleFriendRequests(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError, JsonProcessingException {
        user.setAcceptingFriends(!user.isAcceptingFriends());
        this.userStorageProvider.updateUser(user);
    }

    @Override
    public void removeAllFriends(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.friendClearRequest.executeRequest(id, this.serverTokenQuery.getToken());
    }

    @Override
    public void forceFriend(@NotNull String firstId, @Nullable String secondId, @NotNull String issuer) throws Unauthorized, BadRequest, NotFound, InternalServerError, JsonProcessingException {
        Friendship punishment = new UserFriendship(firstId, secondId, FriendshipAction.FORCE, false, issuer);
        this.friendCreateRequest.executeRequest(
                this.mapper.writeValueAsString(punishment),
                this.serverTokenQuery.getToken()
        );
        this.friendshipChannel.sendMessage(punishment);
    }
}
