package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface GameLoginManager {

    void matchPlayerJoin(@NotNull Match match, @NotNull User user, @NotNull Player player);

}