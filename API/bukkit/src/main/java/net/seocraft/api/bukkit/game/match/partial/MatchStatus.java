package net.seocraft.api.bukkit.game.match.partial;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MatchStatus {
    @JsonProperty("Preparing")
    PREPARING,
    @JsonProperty("Waiting")
    WAITING,
    @JsonProperty("Starting")
    STARTING,
    @JsonProperty("Ingame")
    INGAME,
    @JsonProperty("Finished")
    FINISHED,
    @JsonProperty("Invalidated")
    INVALIDATED,
    @JsonProperty("Forced")
    FORCED
}
