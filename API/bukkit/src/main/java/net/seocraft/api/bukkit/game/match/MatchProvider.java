package net.seocraft.api.bukkit.game.match;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.ListenableFuture;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface MatchProvider {

    @NotNull Match createMatch(
            @NotNull String map,
            @NotNull Set<Team> teams,
            @NotNull String gamemode,
            @NotNull String subGamemode
    ) throws InternalServerError, IOException, Unauthorized, NotFound, BadRequest;

    @NotNull ListenableFuture<AsyncResponse<Set<Match>>> findMatch(
            @Nullable String gamemode,
            @Nullable String subGamemode,
            @Nullable String map,
            @NotNull MatchStatus status
    );

    @NotNull ListenableFuture<AsyncResponse<Match>> getCachedMatch(@NotNull String id);

    @NotNull Match getCachedMatchSync(@NotNull String id) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull ListenableFuture<AsyncResponse<Match>> findMatchById(@NotNull String id);

    @NotNull Match findMatchByIdSync(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull Set<Match> findMatchSync(
            @Nullable String gamemode,
            @Nullable String subGamemode,
            @Nullable String map,
            @NotNull MatchStatus status
    ) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull Match updateMatch(@NotNull Match match) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull Set<Match> getUserWonMatches(@NotNull User user, @NotNull Gamemode gamemode, @NotNull SubGamemode subGamemode) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError;

    @NotNull Set<Match> getServerMatches();

}
