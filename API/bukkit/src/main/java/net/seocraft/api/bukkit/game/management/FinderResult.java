package net.seocraft.api.bukkit.game.management;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.server.Server;
import org.jetbrains.annotations.NotNull;

public interface FinderResult {

    @NotNull Server getServer();

    @NotNull Match getMatch();
}
