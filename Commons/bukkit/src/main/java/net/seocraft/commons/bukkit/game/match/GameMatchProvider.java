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
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.utils.TimeUtils;
import net.seocraft.commons.core.backend.map.MapGetRequest;
import net.seocraft.commons.core.backend.match.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class GameMatchProvider implements MatchProvider {

    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private ListeningExecutorService executorService;
    @Inject private MatchGetRequest matchGetRequest;
    @Inject private MapGetRequest mapGetRequest;
    @Inject private MatchFindRequest matchFindRequest;
    @Inject private MatchUpdateRequest matchUpdateRequest;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private MatchCreateRequest matchCreateRequest;
    @Inject private MatchUserWonRequest matchUserWonRequest;
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

        return this.objectMapper.readValue(
                matchResponse,
                Match.class
        );
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
        return this.objectMapper.readValue(
                this.matchGetRequest.executeRequest(id, this.serverTokenQuery.getToken()),
                Match.class
        );
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

        switch (status) {
            case INGAME: {
                node.put("status", "ingame");
                break;
            }
            case STARTING: {
                node.put("status", "starting");
                break;
            }
            case WAITING: {
                node.put("status", "waiting");
                break;
            }
            default: {
                throw new IllegalArgumentException("You can only pass INGAME/WAITING/STARTING status.");
            }
        }

        String response = this.matchFindRequest.executeRequest(
                this.objectMapper.writeValueAsString(node),
                this.serverTokenQuery.getToken()
        );

        if (response.equalsIgnoreCase("")) return new HashSet<>();
        return this.objectMapper.readValue(
                response,
                new TypeReference<Set<Match>>(){}
        );
    }

    @Override
    public @NotNull Match updateMatch(@NotNull Match match) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {

        String response = this.matchUpdateRequest.executeRequest(
                this.objectMapper.writeValueAsString(match),
                match.getId(),
                this.serverTokenQuery.getToken()
        );

        return this.objectMapper.readValue(response, Match.class);
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
}
