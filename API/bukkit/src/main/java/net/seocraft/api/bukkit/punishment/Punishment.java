package net.seocraft.api.bukkit.punishment;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Punishment extends Model {

    @JsonProperty("type")
    @NotNull PunishmentType getPunishmentType();

    @JsonProperty("punisher")
    @NotNull String getPunisherId();

    @JsonProperty("punished")
    @NotNull String getPunishedId();

    @Nullable String getServer();

    @Nullable Match getMatch();

    @JsonProperty("last_ip")
    @Nullable String getLastIp();

    @NotNull String getReason();

    @JsonProperty("expires")
    long getExpiration();

    @JsonProperty("created_at")
    long getCreatedAt();

    boolean isAutomatic();

    boolean isAppealed();

    boolean isSilent();

    boolean isActive();

    void setActive(boolean active);

    default boolean isPermanent() {
        return getExpiration() < 0;
    }

}