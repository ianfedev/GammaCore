package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.MatchFinder;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class GameMatchFinder implements MatchFinder {

    @Inject private MatchProvider matchProvider;
    @Inject private ServerManager serverManager;

    public @NotNull FinderResult findAvailableMatch(@NotNull String gamemode, @NotNull String subGamemode) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {
        Set<Match> matchList = this.matchProvider.findMatchSync(gamemode, subGamemode, null);
        if (!matchList.isEmpty()) {
            Match selectedMatch = matchList.stream().findAny().get();
            Optional<Server> server = this.serverManager.getServerByQuerySync(
                    null,
                    selectedMatch.getId(),
                    null,
                    null,
                    null
            ).stream().findAny();

            if (server.isPresent()) {
                return new GameResult(server.get(), selectedMatch);
            } else {
               throw new InternalServerError("Error obtaining match server");
            }
        } else {
            Optional<Server> server = this.serverManager.getServerByQuerySync(
                    null,
                    null,
                    null,
                    null,
                    "SLUG-HERE" // TODO: Create new cloud server and return uuid
            ).stream().findAny();

            if (server.isPresent()) {
                Server foundServer = server.get();
                if (!foundServer.getMatches().isEmpty()) {
                    String match = null;
                    for (String matches : server.get().getMatches()) {
                        match = matches;
                        break;
                    }
                    assert match != null;
                    Match selectedMatch = this.matchProvider.findMatchByIdSync(match);
                    return new GameResult(foundServer, selectedMatch);
                } else {
                    throw new InternalServerError("Error obtaining match server");
                }
            } else {
                throw new InternalServerError("Error obtaining match server");
            }
        }
    }
}
