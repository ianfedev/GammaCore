package net.seocraft.api.bukkit.user;

import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.models.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UserStoreHandler {

    void cacheStoreUser(@NotNull User user);

    @NotNull ListenableFuture<AsyncResponse<User>> getCachedUser(@NotNull String id);

    @Nullable ListenableFuture<AsyncResponse<User>> findUserRecord(@NotNull String username);

    @NotNull User getCachedUserSync(@NotNull String id);

    @NotNull User getUser(@NotNull String id);

}