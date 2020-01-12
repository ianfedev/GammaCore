package net.seocraft.api.bukkit.channel.admin;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

public interface ACMentionParser {

    @NotNull Set<User> getMentionedUsers(@NotNull String rawMessage) throws Unauthorized, IOException, BadRequest, InternalServerError;
}
