package net.seocraft.commons.bukkit.punishment;

import net.seocraft.api.shared.models.Match;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IPunishment implements Punishment {

    private @NotNull String id;
    private @NotNull PunishmentType punishmentType;
    private @NotNull String punisherId;
    private @NotNull String punishedId;
    private @NotNull String server;
    private @Nullable Match match;
    private @NotNull String lastIp;
    private @NotNull String reason;
    private long expiration;
    private long createdAt;
    private boolean automatic;
    private boolean appealed;
    private boolean silent;
    private boolean active;

    public IPunishment(@NotNull String id, @NotNull PunishmentType punishmentType, @NotNull String punisherId, @NotNull String punishedId, @NotNull String server, @Nullable Match match, @NotNull String getLastIp, @NotNull String reason, long expiration, long createdAt, boolean automatic, boolean appealed, boolean silent) {
        this.id = id;
        this.punishmentType = punishmentType;
        this.punisherId = punisherId;
        this.punishedId = punishedId;
        this.server = server;
        this.match = match;
        this.lastIp = getLastIp;
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