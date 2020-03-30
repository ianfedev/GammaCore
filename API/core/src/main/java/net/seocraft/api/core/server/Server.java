package net.seocraft.api.core.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Set;

public interface Server extends Model {

    @NotNull String getSlug();

    @JsonProperty("type")
    @NotNull ServerType getServerType();

    @Nullable String getGamemode();

    @JsonProperty("subGamemode")
    @Nullable String getSubGamemode();

    @JsonProperty("maxRunning")
    int getMaxRunning();

    @JsonProperty("maxTotal")
    int getMaxTotal();

    @JsonIgnore
    int getPlayedMatches();

    @JsonProperty("createdAt")
    @NotNull Date getStartedAt();

    @JsonProperty("players")
    @NotNull Set<String> getOnlinePlayers();

    void addOnlinePlayer(String id);

    void removeOnlinePlayer(String id);

    @NotNull String getCluster();

    @NotNull Set<String> getMatches();

    void addMatch(String id);

}
