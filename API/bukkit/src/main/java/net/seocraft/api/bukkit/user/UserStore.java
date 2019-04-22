package net.seocraft.api.bukkit.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.redis.RedisClient;
import net.seocraft.api.shared.serialization.models.UserDeserializer;
import net.seocraft.api.shared.user.UserDataRequest;

import java.util.concurrent.Executors;

@Singleton
public class UserStore {

    @Inject private UserDataRequest request;
    private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8));
    @Inject private UserDeserializer userDeserializer;
    @Inject private ServerTokenQuery tokenHandler;
    @Inject private Gson gson;
    @Inject private RedisClient client;

    public void storeUser(String name, String model) {
        name = name.toLowerCase();
        this.client.setString(name, model);
        this.client.setExpiration(name, 120);
    }

    public ListenableFuture<User> getUserObject(String name) {
        return this.executorService.submit(() -> getUserObjectSync(name));
    }

    public User getUserObjectSync(String name) {
        name = name.toLowerCase();
        if (this.client.existsKey(name)) {
            return this.gson.fromJson(this.client.getString(name), User.class);
        } else {
            return getUser(name);
        }
    }

    private User getUser(String name) {
        try {
            User deserializeUser = this.userDeserializer.deserializeModel(
                    this.request.executeRequest(name, this.tokenHandler.getToken())
            );
            storeUser(name, this.gson.toJson(deserializeUser, User.class));
            return deserializeUser;
        } catch (Unauthorized | InternalServerError | NotFound | BadRequest error) {
            return null;
        }
    }

}