package net.seocraft.commons.bukkit.channel.admin;

import net.seocraft.api.bukkit.channel.admin.ACBroadcaster;
import net.seocraft.api.bukkit.channel.admin.ACMessage;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GammaACBroadcaster implements ACBroadcaster {

    @NotNull private Set<User> participants;

    public GammaACBroadcaster(@NotNull Set<User> participants) {
        this.participants = participants;
    }

    @Override
    public void deliveryMessage(@NotNull ACMessage message, @NotNull Set<User> participants) {

    }

}
