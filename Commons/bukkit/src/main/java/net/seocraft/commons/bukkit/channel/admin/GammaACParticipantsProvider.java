package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.channel.admin.ACParticipantsProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserPermissionChecker;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class GammaACParticipantsProvider implements ACParticipantsProvider {

    @Inject private OnlineStatusManager onlineStatusManager;
    @Inject private UserPermissionChecker userPermissionChecker;

    @Override
    public @NotNull Set<User> getChannelParticipants() {
        return this.onlineStatusManager.getOnlinePlayers()
                .stream()
                .filter((user) -> {
                    try {
                        return this.userPermissionChecker.hasPermission(user.getId(), "commons.staff.chat");
                    } catch (Unauthorized | IOException | BadRequest | NotFound | InternalServerError ignore) {}
                    return false;
                })
                .collect(Collectors.toSet());
    }

}
