package net.seocraft.api.shared.models;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Getter @Setter
public class Server implements Model {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String id;
    private String slug;
    private Type type;
    @Nullable private String gamemode;
    @SerializedName("sub_gamemode") @Nullable private String subGamemode;
    @SerializedName("max_running") @Nullable private int maxRunning;
    @SerializedName("max_total") @Nullable private int maxTotal;
    @SerializedName("played_matches") @Nullable private int playedMatches;
    @SerializedName("started_at") private String startedAt;
    private List<String> players;
    private String cluster;
    @Nullable private List<String> matches;

    public Server(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public enum Type {
        LOBBY, GAME, SPECIAL, BUNGEE
    }
}
