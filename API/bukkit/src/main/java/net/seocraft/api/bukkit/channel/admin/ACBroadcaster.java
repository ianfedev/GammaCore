package net.seocraft.api.bukkit.channel.admin;

import org.jetbrains.annotations.NotNull;

public interface ACBroadcaster {

    void deliveryMessage(@NotNull ACMessage message);

}
