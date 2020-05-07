package net.seocraft.commons.bukkit.game.match;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchAssignationProvider;
import net.seocraft.api.bukkit.game.match.MatchCacheManager;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.utils.TimeUtils;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.backend.map.MapGetRequest;
import net.seocraft.commons.core.backend.match.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GameMatchProvider implements MatchProvider {

    @Inject private MatchGetRequest matchGetRequest;
    @Inject private MapGetRequest mapGetRequest;
    @Inject private MatchFindRequest matchFindRequest;
    @Inject private MatchUpdateRequest matchUpdateRequest;
    @Inject private MatchCreateRequest matchCreateRequest;
    @Inject private MatchUserWonRequest matchUserWonRequest;

    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private MatchCacheManager matchCacheManager;
    @Inject private CommonsBukkit commonsBukkit;
    @Inject private RedisClient redisClient;
    @Inject private ListeningExecutorService executorService;
    @Inject private MatchAssignationProvider matchAssignationProvider;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private ObjectMapper objectMapper;

    @Override
    public @NotNull Match createMatch(@NotNull String map, @NotNull Set<Team> teams, @NotNull String gamemode, @NotNull String subGamemode) throws InternalServerError, IOException, Unauthorized, NotFound, BadRequest {

        Gamemode gamemodeRecord;
        try {
            gamemodeRecord = Optional
                    .ofNullable(this.gamemodeProvider.getGamemodeSync(gamemode))
                    .orElseThrow(() -> new InternalServerError("Error finding author record."));
        } catch (Unauthorized | BadRequest | NotFound | IOException | InternalServerError unauthorized) {
            throw new InternalServerError("Error finding author record.");
        }

        Optional<SubGamemode> subGamemodeRecord = gamemodeRecord.getSubGamemodes()
                .stream()
                .filter(record -> record.getId().equalsIgnoreCase(subGamemode))
                .findFirst();

        if (!subGamemodeRecord.isPresent()) throw new InternalServerError("Sub Gamemode not found");

        GameMap mapRecord;
        try {
            mapRecord = this.objectMapper.readValue(
                    this.mapGetRequest.executeRequest(map, this.serverTokenQuery.getToken()),
                    GameMap.class
            );
        } catch (Unauthorized | BadRequest | NotFound | IOException ex) {
            throw new InternalServerError("Map not found");
        }

        String matchResponse = this.matchCreateRequest.executeRequest(
                this.objectMapper.writeValueAsString(
                        new GameMatch(
                                UUID.randomUUID().toString(),
                                mapRecord.getId(),
                                TimeUtils.getUnixStamp(new Date()),
                                teams,
                                MatchStatus.PREPARING,
                                new HashSet<>(),
                                gamemodeRecord.getId(),
                                subGamemodeRecord.get().getId()
                        )
                ),
                this.serverTokenQuery.getToken()
        );

        Match receivedMatch = this.objectMapper.readValue(matchResponse, Match.class);
        this.matchCacheManager.cacheMatch(receivedMatch);
        return this.matchAssignationProvider.setMatchAssignation(receivedMatch);
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Set<Match>>> findMatch(@Nullable String gamemode, @Nullable String subGamemode, @Nullable String map, @NotNull MatchStatus status) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, this.findMatchSync(gamemode, subGamemode, map, status));
            } catch (IOException | Unauthorized | BadRequest | NotFound | InternalServerError e) {
                return new AsyncResponse<>(e, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Match>> getCachedMatch(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, this.getCachedMatchSync(id));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull Match getCachedMatchSync(@NotNull String id) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        if (this.redisClient.existsKey("matchCache:" + id)) {
            return this.matchAssignationProvider.setMatchAssignation(
                    this.objectMapper.readValue(this.redisClient.getString("matchCache:" + id), Match.class)
            );
        } else {
            return findMatchByIdSync(id);
        }
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Match>> findMatchById(@NotNull String id) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, this.findMatchByIdSync(id));
            } catch (IOException | Unauthorized | BadRequest | NotFound | InternalServerError e) {
                return new AsyncResponse<>(e, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull Match findMatchByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        Match match = this.objectMapper.readValue(
                this.matchGetRequest.executeRequest(id, this.serverTokenQuery.getToken()),
                Match.class
        );
        return this.matchAssignationProvider.setMatchAssignation(match);
    }

    @Override
    public @NotNull Set<Match> findMatchSync(@Nullable String gamemode, @Nullable String subGamemode, @Nullable String map, @NotNull MatchStatus status) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        ObjectNode node = this.objectMapper.createObjectNode();

        if (map != null) node.put("map", map);
        if (gamemode != null) {
            node.put("gamemode", gamemode);
            if (subGamemode == null) throw new IllegalArgumentException("You can not send a gamemode without sub-gamemode.");
            node.put("subGamemode", subGamemode);
        }

        if (map == null && gamemode == null && subGamemode == null) throw new IllegalArgumentException("No query specified.");

        String response = this.matchFindRequest.executeRequest(
                this.objectMapper.writeValueAsString(node),
                this.serverTokenQuery.getToken()
        );

        if (response.equalsIgnoreCase("")) return new HashSet<>();
        Set<Match> matches = this.objectMapper.readValue(
                response,
                new TypeReference<Set<Match>>(){}
        );

        return matches.stream().map(match -> this.matchAssignationProvider.setMatchAssignation(match)).collect(Collectors.toSet());
    }

    @Override
    public @NotNull Match updateMatch(@NotNull Match match) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {

        String response = this.matchUpdateRequest.executeRequest(
                this.objectMapper.writeValueAsString(match),
                match.getId(),
                this.serverTokenQuery.getToken()
        );

        this.matchCacheManager.invalidateCache(match.getId());
        Match finalMatch = this.objectMapper.readValue(response, Match.class);
        this.matchCacheManager.cacheMatch(finalMatch);
        return this.matchAssignationProvider.setMatchAssignation(finalMatch);
    }

    @Override
    public @NotNull Set<Match> getUserWonMatches(@NotNull User user, @NotNull Gamemode gamemode, @NotNull SubGamemode subGamemode) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        ObjectNode node = this.objectMapper.createObjectNode();
        node.put("user", user.getId());
        node.put("gamemode", gamemode.getId());
        node.put("subGamemode", subGamemode.getId());

        String response = this.matchUserWonRequest.executeRequest(
                this.objectMapper.writeValueAsString(node),
                this.serverTokenQuery.getToken()
        );

        return this.objectMapper.readValue(
                response,
                new TypeReference<Set<Match>>(){}
        );

    }

    @Override
    public @NotNull Set<Match> getServerMatches() {
        return this.commonsBukkit.getServerRecord().getMatches()
                .stream()
                .map(match -> {
                    try {
                        return this.getCachedMatchSync(match);
                    } catch (IOException | Unauthorized | BadRequest | NotFound | InternalServerError e) {
                        Bukkit.getLogger().log(Level.WARNING, "There was an error retreiving a match from server list", e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
