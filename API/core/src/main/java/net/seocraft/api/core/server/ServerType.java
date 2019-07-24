package net.seocraft.api.core.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ServerType {
    @JsonProperty("lobby")
    LOBBY,
    @JsonProperty("game")
    GAME,
    @JsonProperty("special")
    SPECIAL,
    @JsonProperty("bungee")
    BUNGEE
}
