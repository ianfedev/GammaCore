package net.seocraft.api.core.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ServerType {
    @JsonProperty("Lobby")
    LOBBY,
    @JsonProperty("Game")
    GAME,
    @JsonProperty("Special")
    SPECIAL,
    @JsonProperty("Bungee")
    BUNGEE
}
