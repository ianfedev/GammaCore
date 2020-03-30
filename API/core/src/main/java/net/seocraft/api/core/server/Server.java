package net.seocraft.api.core.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Set;

public interface Server extends Model {

    @NotNull String getSlug();

    @NotNull ServerType getServerType();

    @Nullable String getGamemode();

    @Nullable String getSubGamemode();

    int getMaxRunning();

    int getMaxTotal();

    @JsonIgnore
    int getPlayedMatches();

    @NotNull Date getStartedAt();

    @NotNull Set<String> getOnlinePlayers();

    void addOnlinePlayer(String id);

    void removeOnlinePlayer(String id);

    @NotNull String getCluster();

    @NotNull Set<String> getMatches();

    void addMatch(String id);

}
