package net.seocraft.commons.bukkit.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.stats.Stats;
import net.seocraft.api.bukkit.stats.StatsProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.core.backend.stats.StatsGetRequest;
import net.seocraft.commons.core.backend.stats.StatsUpdateRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GameStatsProvider implements StatsProvider {

    @Inject private StatsGetRequest statsGetRequest;
    @Inject private ObjectMapper mapper;
    @Inject private StatsUpdateRequest statsUpdateRequest;
    @Inject private ServerTokenQuery serverTokenQuery;

    @Override
    public @NotNull Stats getPlayerStats(@NotNull User user) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {

        String stats = this.statsGetRequest.executeRequest(
                user.getId(),
                this.serverTokenQuery.getToken()
        );

        return this.mapper.readValue(stats, Stats.class);
    }

    @Override
    public @NotNull Stats updatePlayerStats(@NotNull Stats stats) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {

        String rawStats = this.mapper.writeValueAsString(stats);

        String response = this.statsUpdateRequest.executeRequest(
                stats.getId(),
                rawStats,
                this.serverTokenQuery.getToken()
        );

        return this.mapper.readValue(response, Stats.class);
    }
}
