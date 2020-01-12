package net.seocraft.api.bukkit.channel.admin;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ACMessageManager {

    @NotNull Set<User> getChannelParticipants();

    void sendMessage(@NotNull String message, boolean important);

}
