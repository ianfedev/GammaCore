package net.seocraft.api.shared.models;

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
    @Nullable private String sub_gamemode;
    @Nullable private Integer max_running;
    @Nullable private Integer max_total;
    @Nullable private Integer played_matches;
    private String started_at;
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
