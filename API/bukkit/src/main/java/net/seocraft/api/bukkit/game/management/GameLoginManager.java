package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface GameLoginManager {

    void matchPlayerJoin(@NotNull FinderResult result, @NotNull User user, @NotNull Player player);

    void matchPlayerLeave(@NotNull Match match, @NotNull User user, @NotNull Player player) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException;

}