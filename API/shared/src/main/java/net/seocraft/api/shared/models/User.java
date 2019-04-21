package net.seocraft.api.shared.models;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

@Getter @Setter
public class User implements Model {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @SerializedName("_id") private String id;
    private String username;
    @SerializedName("username_lowercase") private String usernameLowercase;
    @Nullable private String email;
    private Map<String, Group> groups;
    private String skin;
    @SerializedName("last_seen") private String lastSeen;
    @SerializedName("last_game") private String lastGame;
    @SerializedName("member_since") private Integer memberSince;
    private Boolean verified;
    private Integer level;
    private Integer experience;
    @SerializedName("used_ips") private Set<IpRecord> usedIps;
    private Boolean disguised;
    @SerializedName("disguise_actual") @Nullable private String disguiseName;
    @SerializedName("disguise_lowercase") @Nullable private String disguiseLowercase;
    @SerializedName("disguise_group") @Nullable private Group disguiseGroup;
    @SerializedName("disguise_history") @Nullable private Set<DisguiseHistory> disguiseHistories;
    private String language;
    @SerializedName("accept_friends") private Boolean acceptFriends;
    @SerializedName("accept_parties") private Boolean acceptParties;
    @SerializedName("show_status") private Boolean showStatus;

    public String id() {
        return id;
    }

    @Getter @Setter
    public class IpRecord {
        private String number;
        private String country;
        private Boolean primary;
    }

    @Getter @Setter
    public class DisguiseHistory {
        private String nickname;
        private Group group;
        private String createdAt;
    }

}
