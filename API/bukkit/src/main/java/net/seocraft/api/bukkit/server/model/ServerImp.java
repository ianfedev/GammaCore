package net.seocraft.api.bukkit.server.model;

import net.seocraft.api.bukkit.game.gamemode.model.Gamemode;
import net.seocraft.api.bukkit.game.subgame.SubGamemode;
import net.seocraft.api.shared.serialization.model.FieldName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class ServerImp implements Server {

    @NotNull private String id;
    @NotNull private String slug;
    @NotNull private ServerType serverType;
    private @Nullable Gamemode gamemode;
    @Nullable private SubGamemode subGamemode;
    private int maxRunning;
    private int maxTotal;
    private long startedAt;
    @NotNull private List<String> onlinePlayers;
    @NotNull private String cluster;
    @NotNull private List<String> matches;

    @ConstructorProperties({"_id", "slug", "type", "gamemode", "sub_gamemode", "max_running", "max_total", "started_at", "players", "cluster", "matches"})
    public ServerImp(@NotNull String id, @NotNull String slug, @NotNull ServerType serverType, @Nullable Gamemode gamemode, @Nullable SubGamemode subGamemode, int maxRunning, int maxTotal, long startedAt, @NotNull List<String> onlinePlayers, @NotNull String cluster, @NotNull List<String> matches) {
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
    public @Nullable Gamemode getGamemode() {
        return this.gamemode;
    }

    @Override
    public @Nullable SubGamemode getSubGamemode() {
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
