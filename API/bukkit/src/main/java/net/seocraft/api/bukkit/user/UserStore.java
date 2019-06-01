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

    @Inject private UserDataRequest request;
    @Inject private ListeningExecutorService executorService;
    @Inject private UserDeserializer userDeserializer;
    @Inject private ServerTokenQuery tokenHandler;
    @Inject private Gson gson;
    @Inject private RedisClient client;


    public void storeUser(User model, UUID uuid) {
        String idString = uuid.toString();
        this.client.setString("user:" + idString, gson.toJson(model));
        this.client.setExpiration("user:" + idString, 120);
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
            storeUser(deserializeUser, id);
            return deserializeUser;
        } catch (Unauthorized | InternalServerError | NotFound | BadRequest error) {
            return null;
        }
    }
}
