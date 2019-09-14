package net.seocraft.commons.bukkit.game.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.map.MapProvider;
import net.seocraft.api.bukkit.game.map.partial.Contribution;
import net.seocraft.api.bukkit.game.map.partial.GameRating;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.game.map.partial.MapContribution;
import net.seocraft.commons.core.backend.map.MapLoadRequest;
import net.seocraft.commons.core.backend.map.MapVoteRequest;
import net.seocraft.commons.core.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CoreMapProvider implements MapProvider {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private ListeningExecutorService executorService;
    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private MapLoadRequest mapLoadRequest;
    @Inject private MapVoteRequest mapVoteRequest;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private ObjectMapper mapper;

    @Override
    public @NotNull ListenableFuture<AsyncResponse<GameMap>> loadMap(@NotNull String name, @NotNull String file, @NotNull String configuration, @NotNull String image, @NotNull String author, @NotNull String version, @NotNull Set<Contribution> contributors, @NotNull String gamemode, @NotNull String subGamemode, @NotNull String description) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, this.loadMapSync(name, file, configuration, image, author, version, contributors, gamemode, subGamemode, description));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public @NotNull GameMap loadMapSync(@NotNull String name, @NotNull String file, @NotNull String configuration, @NotNull String image, @NotNull String author, @NotNull String version, @NotNull Set<Contribution> contributors, @NotNull String gamemode, @NotNull String subGamemode, @NotNull String description) throws InternalServerError, IOException, Unauthorized, NotFound, BadRequest {

        // Check if author is registered

        User authorRecord;
        try {
            authorRecord = this.userStorageProvider.findUserByNameSync(author);
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ex) {
            ex.printStackTrace();
            throw new InternalServerError("Error finding author record.");
        }

        // Check if contributors are registered

        Set<Contribution> fixedContributors = contributors.stream()
                .map(contribution -> {
                    try {
                        User contributorRecord  = this.userStorageProvider.findUserByNameSync(contribution.getContributor());
                        return new MapContribution(contributorRecord.getId(), contribution.getContributor());
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ex) {
                        Bukkit.getLogger().log(
                                Level.WARNING,
                                "[GameAPI] The contributor {0} was not found for map {1}.",
                                new Object[]{contribution.getContributor(), name}
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Gamemode gamemodeRecord;
        try {
            gamemodeRecord = Optional
                    .ofNullable(this.gamemodeProvider.getGamemodeSync(gamemode))
                    .orElseThrow(() -> new InternalServerError("Error finding author record."));
        } catch (Unauthorized | BadRequest | NotFound | IOException unauthorized) {
            unauthorized.printStackTrace();
            throw new InternalServerError("Error finding author record.");
        }

        Optional<SubGamemode> subGamemodeRecord = gamemodeRecord.getSubGamemodes()
                .stream()
                .filter(record -> record.getId().equalsIgnoreCase(subGamemode))
                .findFirst();

        if (!subGamemodeRecord.isPresent()) throw new InternalServerError("Sub Gamemode not found");

        GameMap rawMap = new CoreMap(
                UUID.randomUUID().toString(),
                name,
                file,
                configuration,
                image,
                authorRecord.getId(),
                version,
                fixedContributors,
                gamemodeRecord.getId(),
                subGamemodeRecord.get().getId(),
                description,
                new HashSet<>(),
                TimeUtils.getUnixStamp(new Date())
        );

        String response = this.mapLoadRequest.executeRequest(
                this.mapper.writeValueAsString(rawMap),
                this.serverTokenQuery.getToken()
        );

        return this.mapper.readValue(
                response,
                GameMap.class
        );
    }

    @Override
    public @NotNull ListenableFuture<AsyncResponse<Boolean>> voteMap(@NotNull String mapId, @NotNull String userId, @NotNull GameRating rating) {
        return this.executorService.submit(() -> {
            try {
                return new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, this.voteMapSync(mapId, userId, rating));
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                return new AsyncResponse<>(exception, AsyncResponse.Status.ERROR, null);
            }
        });
    }

    @Override
    public Boolean voteMapSync(@NotNull String mapId, @NotNull String userId, @NotNull GameRating rating) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        ObjectNode request = this.mapper.createObjectNode();
        request.put("map", mapId);
        request.put("user", userId);
        request.put("rating", GameRating.getNumber(rating));
        String response = this.mapVoteRequest.executeRequest(
                this.mapper.writeValueAsString(request),
                this.serverTokenQuery.getToken()
        );
        return this.mapper.readValue(response, Boolean.class);
    }

}
