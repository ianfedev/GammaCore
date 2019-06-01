package net.seocraft.api.shared.models;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class User implements Model {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @SerializedName("_id") private String id;
    private String username;
    @SerializedName("username_lowercase") private String usernameLowercase;
    @Nullable
    private String email;
    private List<Group> groups;
    private String skin;
    @SerializedName("last_seen") private String lastSeen;
    @SerializedName("last_game") private String lastGame;
    @SerializedName("member_since") private int memberSince;
    @SerializedName("uuid") private UUID gameUUID;
    private boolean verified;
    private int level;
    private int experience;
    @SerializedName("used_ips") private Set<IpRecord> usedIps;
    private boolean disguised;
    @SerializedName("disguise_actual") @Nullable private String disguiseName;
    @SerializedName("disguise_lowercase") @Nullable private String disguiseLowercase;
    @SerializedName("disguise_group") @Nullable private Group disguiseGroup;
    @SerializedName("disguise_history") @Nullable private Set<DisguiseHistory> disguiseHistories;
    private String language;
    @SerializedName("accept_friends") private boolean acceptFriends;
    @SerializedName("accept_parties") private boolean acceptParties;
    @SerializedName("show_status") private boolean showStatus;

    public String id() {
        return id;
    }

    @Getter @Setter
    public class IpRecord {
        private String number;
        private String country;
        private boolean primary;
    }

    @Getter @Setter
    public class DisguiseHistory {
        private String nickname;
        private Group group;
        private String createdAt;
    }

}
