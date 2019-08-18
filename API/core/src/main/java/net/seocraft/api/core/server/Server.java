package net.seocraft.api.core.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Server extends Model {

    @NotNull String getSlug();

    @JsonProperty("type")
    @NotNull ServerType getServerType();

    @Nullable String getGamemode();

    @JsonProperty("sub_gamemode")
    @Nullable String getSubGamemode();

    @JsonProperty("max_running")
    int getMaxRunning();

    @JsonProperty("max_total")
    int getMaxTotal();

    @JsonIgnore
    int getPlayedMatches();

    @JsonProperty("started_at")
    long getStartedAt();

    @JsonProperty("players")
    @NotNull Set<String> getOnlinePlayers();

    int getMaxPlayers();

    void addOnlinePlayer(String id);

    void removeOnlinePlayer(String id);

    @NotNull String getCluster();

    @NotNull Set<String> getMatches();

    void addMatch(String id);

}
