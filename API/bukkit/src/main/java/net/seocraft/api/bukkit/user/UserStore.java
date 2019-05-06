package net.seocraft.api.bukkit.user;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.redis.RedisClient;
import net.seocraft.api.shared.serialization.models.UserDeserializer;
import net.seocraft.api.shared.user.UserDataRequest;

import java.util.UUID;

@Singleton
public class UserStore {

    @Inject
    private UserDataRequest request;
    @Inject
    private ListeningExecutorService executorService;
    @Inject
    private UserDeserializer userDeserializer;
    @Inject
    private ServerTokenQuery tokenHandler;
    @Inject
    private Gson gson;
    @Inject
    private RedisClient client;

    /**
     * @deprecated Use storeUser(User) as replacement of this
     */
    @Deprecated
    public void storeUser(String name, User model) {
        storeUser(name, gson.toJson(model));
    }

    /**
     * @deprecated Use storeUser(User) as replacement of this
     */
    @Deprecated
    public void storeUser(String name, String model) {
        name = name.toLowerCase();
        this.client.setString(name, model);
        this.client.setExpiration(name, 120);

        User user = gson.fromJson(model, User.class);
        String id = user.id();

        storeUser(id, user);
    }

    public ListenableFuture<User> getUserObject(String name) {
        return this.executorService.submit(() -> getUserObjectSync(name));
    }

    public User getUserObjectSync(String name) {
        name = name.toLowerCase();
        if (this.client.existsKey("user.name:" + name)) {
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

            storeUser(deserializeUser);
            return deserializeUser;
        } catch (Unauthorized | InternalServerError | NotFound | BadRequest error) {
            return null;
        }
    }


    public void storeUser(User model) {
        this.client.setString("user:" + model.id(), gson.toJson(model));
        this.client.setExpiration(model.id(), 120);

        this.client.setString("user.name:" + model.getUsername(), gson.toJson(model));
        this.client.setExpiration(model.id(), 120);
    }

    public ListenableFuture<User> getUserObject(UUID id) {
        return this.executorService.submit(() -> getUserObjectSync(id));
    }

    public User getUserObjectSync(UUID id) {
        String idString = id.toString();

        if (this.client.existsKey("user:" + idString)) {
            return this.gson.fromJson(this.client.getString("user:" + idString), User.class);
        } else {
            return getUser(id);
        }
    }

    private User getUser(UUID id) {
        String idString = id.toString();

        try {
            User deserializeUser = this.userDeserializer.deserializeModel(
                    this.request.executeRequest(idString, this.tokenHandler.getToken())
            );

            storeUser(deserializeUser);
            return deserializeUser;
        } catch (Unauthorized | InternalServerError | NotFound | BadRequest error) {
            return null;
        }
    }
}