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

    @NotNull ServerType getServerType();

    @Nullable Gamemode getGamemode();

    @Nullable SubGamemode getSubGamemode();

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
