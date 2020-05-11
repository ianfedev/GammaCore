package net.seocraft.commons.bukkit.punishment;

import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.bukkit.punishment.PunishmentType;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;

public class UserPunishment implements Punishment {

    @NotNull private String id;
    @NotNull private PunishmentType punishmentType;
    @NotNull private User issuer;
    @NotNull private User punished;
    @NotNull private String server;
    @Nullable private Match match;
    @Nullable private String lastIp;
    @NotNull private String reason;
    private long expiration;
    private long createdAt;
    private boolean automatic;
    private boolean appealed;
    private boolean silent;
    private boolean active;

    @ConstructorProperties({"_id", "type", "punisher", "punished", "server", "match", "last_ip", "reason", "expires", "created_at", "automatic", "appealed", "silent", "active"})
    UserPunishment(@NotNull String id, @NotNull PunishmentType punishmentType, @NotNull User issuer, @NotNull User punished, @NotNull String server, @Nullable Match match, @Nullable String lastIp, @NotNull String reason, long expiration, long createdAt, boolean automatic, boolean appealed, boolean silent, boolean active) {
        this.id = id;
        this.punishmentType = punishmentType;
        this.issuer = issuer;
        this.punished = punished;
        this.server = server;
        this.match = match;
        this.lastIp = lastIp;
        this.reason = reason;
        this.expiration = expiration;
        this.createdAt = createdAt;
        this.automatic = automatic;
        this.appealed = appealed;
        this.silent = silent;
        this.active = active;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public @NotNull PunishmentType getType() {
        return this.punishmentType;
    }

    @Override
    public @NotNull User getIssuer() {
        return this.issuer;
    }

    @Override
    public @NotNull User getPunished() {
        return punished;
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