package net.seocraft.api.core.server;

import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Server extends Model {

    @NotNull String getSlug();

    @NotNull ServerType getServerType();

    @Nullable String getGamemode();

    @Nullable String getSubGamemode();

    int getMaxRunning();

    int getMaxTotal();

    int getPlayedMatches();

    long getStartedAt();

    @NotNull List<String> getOnlinePlayers();

    void addOnlinePlayer(String id);

    void removeOnlinePlayer(String id);

    @NotNull String getCluster();

    @NotNull List<String> getMatches();

    void addMatch(String id);

}
