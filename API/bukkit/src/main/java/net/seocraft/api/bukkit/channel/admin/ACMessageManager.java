package net.seocraft.api.bukkit.channel.admin;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ACMessageManager {

    void sendMessage(@NotNull String message, @NotNull User sender, boolean important) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException;

}
