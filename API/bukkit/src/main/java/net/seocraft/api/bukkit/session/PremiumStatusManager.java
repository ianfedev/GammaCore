package net.seocraft.api.bukkit.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface PremiumStatusManager {

    boolean togglePremiumStatus(@NotNull User user) throws Unauthorized, JsonProcessingException, BadRequest, NotFound, InternalServerError;

    boolean canEnablePremium(@NotNull User user);

}
