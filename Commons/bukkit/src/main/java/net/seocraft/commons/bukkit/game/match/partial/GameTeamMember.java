package net.seocraft.commons.bukkit.game.match.partial;

import net.seocraft.api.bukkit.game.match.partial.TeamMember;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class GameTeamMember implements TeamMember {

    @NotNull private String user;
    private long joinedAt;

    @ConstructorProperties({"user", "joinedAt"})
    public GameTeamMember(@NotNull String user, long joinedAt) {
        this.user = user;
        this.joinedAt = joinedAt;
    }

    @Override
    public @NotNull String getUser() {
        return user;
    }

    @Override
    public long getJoinedAt() {
        return joinedAt;
    }
}
