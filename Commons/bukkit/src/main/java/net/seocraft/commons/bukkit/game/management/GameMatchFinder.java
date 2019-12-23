package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.MatchFinder;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class GameMatchFinder implements MatchFinder {

    @Inject private MatchProvider matchProvider;
    @Inject private ServerManager serverManager;
    @Inject private CommonsBukkit instance;
    @Inject private CloudManager cloudManager;

    public @NotNull FinderResult findAvailableMatch(@NotNull String gamemode, @NotNull String subGamemode, @NotNull String serverGroup, boolean spectable) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {
        Set<Match> matchList = this.matchProvider.findMatchSync(gamemode, subGamemode, null, MatchStatus.WAITING);
        if (spectable) {
            matchList.addAll(this.matchProvider.findMatchSync(gamemode, subGamemode, null, MatchStatus.STARTING));
            matchList.addAll(this.matchProvider.findMatchSync(gamemode, subGamemode, null, MatchStatus.INGAME));
        }
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
                return new GameResult(server.get(), selectedMatch, spectable);
            } else {
               throw new InternalServerError("Error obtaining match server");
            }
        } else {
            if (this.instance.hasCloudDeploy()) {
                UUID serviceUUID = this.cloudManager.createCloudService(serverGroup);
                while (true) {
                    if (this.cloudManager.isConnected(serviceUUID)) {
                        Optional<Server> server = this.serverManager.getServerByQuerySync(
                                null,
                                null,
                                null,
                                null,
                                this.cloudManager.getSlug(serviceUUID)
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
                                return new GameResult(foundServer, selectedMatch, spectable);
                            } else {
                                throw new InternalServerError("Error obtaining match server");
                            }
                        }
                    }
                }
            } else {
                Bukkit.getLogger().log(Level.INFO, "[GameAPI] Cloud environment not detected, skipping match creation.");
                throw new Unauthorized("Cloud environment not detected.");
            }
        }
    }
}
