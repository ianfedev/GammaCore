package net.seocraft.api.core.server;

import net.seocraft.api.core.storage.Model;
import net.seocraft.api.core.old.serialization.model.FieldName;
import net.seocraft.api.core.old.serialization.model.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Server extends Model {

    @NotNull String getSlug();

    @FieldName("type")
    @NotNull ServerType getServerType();

    @Nullable String getGamemode();

    @FieldName("sub_gamemode")
    @Nullable String getSubGamemode();

    @FieldName("max_running")
    int getMaxRunning();

    @FieldName("max_total")
    int getMaxTotal();

    int getPlayedMatches();

    @FieldName("started_at")
    long getStartedAt();

    @FieldName("players")
    @NotNull List<String> getOnlinePlayers();

    void addOnlinePlayer(String id);

    void removeOnlinePlayer(String id);

    @NotNull String getCluster();

    @NotNull List<String> getMatches();

    void addMatch(String id);

}
