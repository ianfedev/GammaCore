package net.seocraft.commons.core.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class CoreServer implements Server {

    @JsonProperty("_id")
    @NotNull private String id;
    @NotNull private String slug;
    @JsonProperty("type")
    @NotNull private ServerType serverType;
    @Nullable private String gamemode;
    @JsonProperty("sub_gamemode")
    @Nullable private String subGamemode;
    @JsonProperty("max_running")
    private int maxRunning;
    @JsonProperty("max_total")
    private int maxTotal;
    @JsonProperty("started_at")
    private long startedAt;
    @JsonProperty("players")
    @NotNull private List<String> onlinePlayers;
    @NotNull private String cluster;
    @NotNull private List<String> matches;

    @ConstructorProperties({"_id", "slug", "type", "gamemode", "sub_gamemode", "max_running", "max_total", "started_at", "players", "cluster", "matches"})
    public CoreServer(@NotNull String id, @NotNull String slug, @NotNull ServerType serverType, @Nullable String gamemode, @Nullable String subGamemode, int maxRunning, int maxTotal, long startedAt, @NotNull List<String> onlinePlayers, @NotNull String cluster, @NotNull List<String> matches) {
        this.id = id;
        this.slug = slug;
        this.serverType = serverType;
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.maxRunning = maxRunning;
        this.maxTotal = maxTotal;
        this.startedAt = startedAt;
        this.onlinePlayers = onlinePlayers;
        this.cluster = cluster;
        this.matches = matches;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public @NotNull String getSlug() {
        return this.slug;
    }

    @Override
    public @NotNull ServerType getServerType() {
        return this.serverType;
    }

    @Override
    public @Nullable String getGamemode() {
        return this.gamemode;
    }

    @Override
    public @Nullable String getSubGamemode() {
        return this.subGamemode;
    }

    @Override
    public int getMaxRunning() {
        return this.maxRunning;
    }

    @Override
    public int getMaxTotal() {
        return this.maxTotal;
    }

    @Override
    public int getPlayedMatches() {
        return this.matches.size();
    }

    @Override
    public long getStartedAt() {
        return this.startedAt;
    }

    @Override
    public @NotNull List<String> getOnlinePlayers() {
        return new ArrayList<>(this.onlinePlayers);
    }

    @Override
    public void addOnlinePlayer(String id) {
        this.onlinePlayers.add(id);
    }

    @Override
    public void removeOnlinePlayer(String id) {
        this.onlinePlayers.remove(id);
    }

    @Override
    public @NotNull String getCluster() {
        return this.cluster;
    }

    @Override
    public @NotNull List<String> getMatches() {
        return this.matches;
    }

    @Override
    public void addMatch(String id) {
        this.matches.add(id);
    }
}
