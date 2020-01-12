package net.seocraft.api.bukkit.channel.admin;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ACBroadcaster {

    void deliveryMessage(@NotNull ACMessage message, @NotNull Set<User> participants);

}
