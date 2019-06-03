package net.seocraft.commons.bukkit.punishment;

import net.seocraft.api.shared.models.Match;
import net.seocraft.api.shared.models.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Punishment extends Model {

    @NotNull PunishmentType getPunishmentType();

    @NotNull String getPunisherId();

    @NotNull String getPunishedId();

    @Nullable String getServer();

    @Nullable String getLastIp();

    @Nullable Match getMatch();

    @NotNull String getReason();

    long getExpiration();

    long getCreatedAt();

    boolean isAutomatic();

    boolean isAppealed();

    boolean isSilent();

    boolean isActive();

    void setActive(boolean active);

    default boolean isPermanent() {
        return getExpiration() > 0;
    }

}