package net.seocraft.commons.bukkit.punishment;

import com.google.gson.annotations.SerializedName;
import net.seocraft.api.shared.models.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IPunishment implements Punishment {

    @SerializedName("_id") private @NotNull String id;
    @SerializedName("type") private @NotNull PunishmentType punishmentType;
    @SerializedName("punisher") private @NotNull String punisherId;
    @SerializedName("punished") private @NotNull String punishedId;
    private @NotNull String server;
    private @Nullable Match match;
    @SerializedName("last_ip") private @Nullable String lastIp;
    private @NotNull String reason;
    @SerializedName("expires") private long expiration;
    @SerializedName("created_at") private long createdAt;
    private boolean automatic;
    private boolean appealed;
    private boolean silent;
    private boolean active;

    IPunishment(@NotNull String id, @NotNull PunishmentType punishmentType, @NotNull String punisherId, @NotNull String punishedId, @NotNull String server, @Nullable Match match, @Nullable String lastIp, @NotNull String reason, long expiration, long createdAt, boolean automatic, boolean appealed, boolean silent) {
        this.id = id;
        this.punishmentType = punishmentType;
        this.punisherId = punisherId;
        this.punishedId = punishedId;
        this.server = server;
        this.match = match;
        this.lastIp = lastIp;
        this.reason = reason;
        this.expiration = expiration;
        this.createdAt = createdAt;
        this.automatic = automatic;
        this.appealed = appealed;
        this.silent = silent;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public @NotNull PunishmentType getPunishmentType() {
        return this.punishmentType;
    }

    @Override
    public @NotNull String getPunisherId() {
        return punisherId;
    }

    @Override
    public @NotNull String getPunishedId() {
        return punishedId;
    }

    @Override
    public @NotNull String getServer() {
        return server;
    }

    @Override
    public @Nullable String getLastIp() {
        return lastIp;
    }

    @Override
    public @Nullable Match getMatch() {
        return match;
    }

    @Override
    public @NotNull String getReason() {
        return reason;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean isAutomatic() {
        return automatic;
    }

    @Override
    public boolean isAppealed() {
        return appealed;
    }

    @Override
    public boolean isSilent() {
        return silent;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

}