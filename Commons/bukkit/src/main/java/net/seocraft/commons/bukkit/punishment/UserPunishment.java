package net.seocraft.commons.bukkit.punishment;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.bukkit.punishment.PunishmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;

public class UserPunishment implements Punishment {

    @JsonProperty("_id")
    @NotNull private String id;
    @JsonProperty("type")
    @NotNull private PunishmentType punishmentType;
    @JsonProperty("punisher")
    @NotNull private String punisherId;
    @JsonProperty("punished")
    @NotNull private String punishedId;
    @NotNull private String server;
    @Nullable private Match match;
    @JsonProperty("last_ip")
    @Nullable private String lastIp;
    @NotNull private String reason;
    @JsonProperty("expires")
    private long expiration;
    @JsonProperty("created_at")
    private long createdAt;
    private boolean automatic;
    private boolean appealed;
    private boolean silent;
    private boolean active;

    @ConstructorProperties({"_id", "type", "punisher", "punished", "server", "match", "last_ip", "expires", "created_at", "automatic", "appealed", "silent", "active"})
    UserPunishment(@NotNull String id, @NotNull PunishmentType punishmentType, @NotNull String punisherId, @NotNull String punishedId, @NotNull String server, @Nullable Match match, @Nullable String lastIp, @NotNull String reason, long expiration, long createdAt, boolean automatic, boolean appealed, boolean silent) {
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