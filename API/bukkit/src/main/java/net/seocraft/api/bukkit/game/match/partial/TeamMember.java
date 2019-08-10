package net.seocraft.api.bukkit.game.match.partial;

import org.jetbrains.annotations.NotNull;

public interface TeamMember {

    @NotNull String getUser();

    long getJoinedAt();
}
