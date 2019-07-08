package net.seocraft.api.bukkit.server.model;

import net.seocraft.api.bukkit.game.gamemode.model.Gamemode;
import net.seocraft.api.bukkit.game.subgame.SubGamemode;
import net.seocraft.api.shared.model.Model;
import net.seocraft.api.shared.serialization.model.FieldName;
import net.seocraft.api.shared.serialization.model.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ImplementedBy(ServerImp.class)
public interface Server extends Model {

    @NotNull String getSlug();

    @FieldName("type")
    @NotNull ServerType getServerType();

    @Nullable Gamemode getGamemode();

    @FieldName("sub_gamemode")
    @Nullable SubGamemode getSubGamemode();

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
