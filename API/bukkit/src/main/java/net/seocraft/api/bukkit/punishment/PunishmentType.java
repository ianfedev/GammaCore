package net.seocraft.api.bukkit.punishment;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PunishmentType {
    @JsonProperty("Ban") BAN,
    @JsonProperty("Kick") KICK,
    @JsonProperty("Warn") WARN
}
