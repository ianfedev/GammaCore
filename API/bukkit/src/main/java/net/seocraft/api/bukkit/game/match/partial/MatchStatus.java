package net.seocraft.api.bukkit.game.match.partial;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MatchStatus {
    @JsonProperty("waiting")
    WAITING,
    @JsonProperty("starting")
    STARTING,
    @JsonProperty("ingame")
    INGAME,
    @JsonProperty("finished")
    FINISHED,
    @JsonProperty("invalidated")
    INVALIDATED,
    @JsonProperty("forced")
    FORCED
}
