package net.seocraft.commons.bukkit.channel.admin.listener;

import net.seocraft.api.bukkit.channel.admin.ACLoginBroadcaster;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.messager.ChannelListener;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserPermissionChecker;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ACLogoutListener implements ChannelListener<User> {

    @NotNull private ACLoginBroadcaster broadcaster;
    @NotNull private UserPermissionChecker userPermissionChecker;

    public ACLogoutListener(@NotNull ACLoginBroadcaster loginBroadcaster, @NotNull UserPermissionChecker userPermissionChecker) {
        this.broadcaster = loginBroadcaster;
        this.userPermissionChecker = userPermissionChecker;
    }

    @Override
    public void receiveMessage(User object) {
        boolean hasPermission = false;
        try {
            hasPermission = this.userPermissionChecker.hasPermission(object.getId(), "commons.staff.chat.important");
        } catch (Unauthorized | IOException | BadRequest | NotFound | InternalServerError ignore) {}

        this.broadcaster.broadcastLogout(
                object,
                hasPermission
        );
    }

}
