package net.seocraft.api.bukkit.channel.admin;

import net.seocraft.api.bukkit.punishment.Punishment;
import org.jetbrains.annotations.NotNull;

public interface ACPunishmentBroadcaster {

    void broadcastPunishment(@NotNull Punishment punishment);

}
