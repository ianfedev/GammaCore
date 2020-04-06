package net.seocraft.commons.bukkit.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.session.PremiumStatusManager;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import org.jetbrains.annotations.NotNull;

public class GammaPremiumStatusManager implements PremiumStatusManager {

    @Inject private RedisClient redisClient;
    @Inject private UserStorageProvider userStorageProvider;

    @Override
    public boolean togglePremiumStatus(@NotNull User user) throws Unauthorized, JsonProcessingException, BadRequest, NotFound, InternalServerError {
        user.getSessionInfo().setPremium(!user.getSessionInfo().isPremium());
        this.userStorageProvider.updateUser(user);
        return user.getSessionInfo().isPremium();
    }

    @Override
    public boolean canEnablePremium(@NotNull User user) {
        return this.redisClient.existsInSet("premium_connected", user.getId());
    }

}
