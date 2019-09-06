package net.seocraft.api.bukkit.punishment;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PunishmentType {
    @JsonProperty("ban") BAN,
    @JsonProperty("kick") KICK,
    @JsonProperty("warn") WARN
}
