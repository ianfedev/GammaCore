package net.seocraft.commons.bukkit.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.cloud.ServerRedirector;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.MatchFinder;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.event.CustomSelectionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NPCRedirector implements ServerRedirector {

    @Inject
    private CommonsBukkit instance;
    @Inject
    private CloudManager cloudManager;
    @Inject
    private ObjectMapper mapper;
    @Inject
    private MatchFinder matchFinder;
    @Inject
    private RedisClient client;
    @Inject
    private MatchProvider matchProvider;
    @Inject
    private ServerManager serverManager;

    private Cache<UUID, String> pendingResults = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .weakValues()
            .build();

    private Lock taskLock = new ReentrantLock();
    private BukkitTask movePlayerTask;

    public void redirectPlayer(@NotNull Gamemode gamemode, @Nullable SubGamemode subGamemode, @NotNull Player player, boolean perk) {
        if (movePlayerTask == null) {
            try {
                taskLock.lock();
                if (movePlayerTask == null) {
                    startTask();
                }
            } finally {
                taskLock.unlock();
            }
        }

        if (this.instance.hasCloudDeploy()) {
            if (subGamemode == null) {
                this.cloudManager.sendPlayerToGroup(
                        player,
                        gamemode.getLobbyGroup()
                );
            } else {
                if (subGamemode.canSelectMap()) {
                    Bukkit.getPluginManager().callEvent(new CustomSelectionEvent(player, gamemode, subGamemode, perk));
                } else {
                    FinderResult result;
                    try {
                        result = this.matchFinder.findAvailableMatch(gamemode.getId(), subGamemode.getId(), subGamemode.getServerGroup(), false);
                        if (result.getMatch().getStatus() == MatchStatus.PREPARING) {
                            pendingResults.put(player.getUniqueId(), result.getMatch().getId());
                            ChatAlertLibrary.infoAlert(player, "The match is being prepared, please wait.");

                            return;
                        }

                        connectToMatch(player, result);
                    } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
                        ChatAlertLibrary.errorChatAlert(player, "Error pairing game, please try again.");
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "This server is not deployed in a cloud. Request denied.");
        }
    }

    private void connectToMatch(Player player, FinderResult result) throws JsonProcessingException {
        String finderResult = this.mapper.writeValueAsString(result);
        this.client.setString(
                "pairing:" + player.getDatabaseIdentifier(),
                finderResult
        );
        this.client.setExpiration("pairing" + player.getDatabaseIdentifier(), 60);
        this.cloudManager.sendPlayerToServer(player, result.getServer().getSlug());
    }

    private void startTask() {
        movePlayerTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, String> entry : pendingResults.asMap().entrySet()) {
                    UUID playerId = entry.getKey();
                    String matchId = entry.getValue();

                    Player player = Bukkit.getPlayer(playerId);

                    if (player == null) {
                        pendingResults.invalidate(playerId);

                        continue;
                    }

                    try {
                        Match match = matchProvider.findMatchByIdSync(matchId);

                        if (match.getStatus() == MatchStatus.WAITING) {
                            Optional<Server> server = serverManager.getServerByQuerySync(
                                    null,
                                    match.getId(),
                                    null,
                                    null,
                                    null
                            ).stream().findAny();

                            if (!server.isPresent()) {
                                ChatAlertLibrary.errorChatAlert(player, "Error pairing game, please try again.");
                                pendingResults.invalidate(playerId);

                                continue;
                            }

                            FinderResult result = createResult(match, server.get());

                            connectToMatch(player, result);

                            pendingResults.invalidate(playerId);

                        } else if (match.getStatus() != MatchStatus.PREPARING) {
                            ChatAlertLibrary.errorChatAlert(player, "Error pairing game, please try again.");

                            pendingResults.invalidate(playerId);
                        }
                    } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
                        ChatAlertLibrary.errorChatAlert(player, "Error pairing game, please try again.");
                        ex.printStackTrace();
                    }

                }
            }
        }.runTaskTimerAsynchronously(instance, 20, 20);
    }

    private FinderResult createResult(Match match, Server server) {
        class FakeFinderResult implements FinderResult {

            @Override
            public @NotNull Server getServer() {
                return server;
            }

            @Override
            public @NotNull Match getMatch() {
                return match;
            }

            @Override
            public boolean isSpectable() {
                return false;
            }
        }

        return new FakeFinderResult();
    }
}
