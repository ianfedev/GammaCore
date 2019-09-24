package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface GameStartManager {

    void startMatchCountdown(@NotNull Match match);

    void forceMatchCountdown(@NotNull Match match, int seconds, @NotNull User issuer, boolean silent);

    void cancelMatchCountdown(@NotNull Match match);

    void cancelMatchCountdown(@NotNull Match match, @NotNull User user, boolean silent);

}
