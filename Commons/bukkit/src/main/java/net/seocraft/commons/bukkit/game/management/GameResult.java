package net.seocraft.commons.bukkit.game.management;

import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.server.Server;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class GameResult implements FinderResult {

    @NotNull private Server server;
    @NotNull private Match match;
    private boolean spectable;

    @ConstructorProperties({"server", "match", "spectable"})
    public GameResult(@NotNull Server server, @NotNull Match match, boolean spectable) {
        this.server = server;
        this.match = match;
        this.spectable = spectable;
    }

    @Override
    public @NotNull Server getServer() {
        return this.server;
    }

    @Override
    public @NotNull Match getMatch() {
        return this.match;
    }

    @Override
    public boolean isSpectable() {
        return this.spectable;
    }
}
