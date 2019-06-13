package net.seocraft.api.shared.user.model;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.seocraft.api.shared.model.Group;
import net.seocraft.api.shared.model.Model;
import net.seocraft.api.shared.serialization.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public class UserImp implements User {

    @SerializedName("_id") private @NotNull String id;
    private @NotNull String username;
    private @Nullable String email;
    private List<Group> groups;
    private String skin;
    @SerializedName("last_seen") private long lastSeen;
    @SerializedName("last_game") private @NotNull String lastGame;
    @SerializedName("member_since") private long memberSince;
    private boolean verified;
    private int level;
    private long experience;
    @SerializedName("used_ips") private List<IpRecord> ipRecord;
    private boolean disguised;
    @SerializedName("disguise_actual") @Nullable private String disguiseName;
    @SerializedName("disguise_lowercase") @Nullable private String disguiseLowercase;
    @SerializedName("disguise_group") @Nullable private Group disguiseGroup;
    @SerializedName("disguise_history") @Nullable private List<DisguiseHistory> disguiseHistory;
    private @NotNull String language;
    @SerializedName("accept_friends") private boolean acceptFriends;
    @SerializedName("accept_parties") private boolean acceptParties;
    @SerializedName("show_status") private boolean showStatus;

    public String id() {
        return this.id;
    }

    @Override
    public @NotNull String getUsername() {
        return this.username;
    }

    @Override
    public @Nullable String getEmail() {
        return this.email;
    }

    @Override
    public @NotNull List<Group> getGroups() {
        return this.groups;
    }

    @Override
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public @NotNull Group getPrimaryGroup() {
        Group primaryGroup = new Group();
        primaryGroup.setPriority(999999999); // Change priority if needed deeper groups
        for (Group group: getGroups()) {
            if (group.getPriority() < primaryGroup.getPriority()) primaryGroup = group;
        }
        return primaryGroup;
    }

    @Override
    public @NotNull String getSkin() {
        return this.skin;
    }

    @Override
    public void setSkin(@NotNull String skin) {
        this.skin = skin;
    }

    @Override
    public @NotNull Date getLastSeen() {
        return TimeUtils.parseUnixStamp((int) this.lastSeen);
    }

    @Override
    public @NotNull String getLastGame() {
        return this.lastGame;
    }

    @Override
    public @NotNull Date getMemberSince() {
        return TimeUtils.parseUnixStamp((int) this.memberSince);
    }

    @Override
    public boolean isVerified() {
        return this.verified;
    }

    @Override
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public long getExperience() {
        return this.experience;
    }

    @Override
    public void addExperience(long experience) {
        this.experience += experience;
    }

    @Override
    public void removeExperience(long experience) {
        this.experience -= experience;
    }

    @Override
    public @NotNull List<IpRecord> getUsedIp() {
        return this.ipRecord;
    }

    @Override
    public boolean isDisguised() {
        return this.disguised;
    }

    @Override
    public void setDisguised(boolean disguised) {
        this.disguised = disguised;
    }

    @Override
    public @Nullable String getDisguiseName() {
        return this.disguiseName;
    }

    @Override
    public void setDisguiseName(@NotNull String name) {
        this.disguiseName = name;
    }

    @Override
    public @Nullable Group getDisguiseGroup() {
        return disguiseGroup;
    }

    @Override
    public void setDisguiseGroup(@NotNull Group group) {
        this.disguiseGroup = group;
    }

    @Override
    public @Nullable List<DisguiseHistory> getDisguiseHistory() {
        return this.disguiseHistory;
    }

    @Override
    public @NotNull String getLanguage() {
        return this.language;
    }

    @Override
    public void setLanguage(@NotNull String language) {
        this.language = language;
    }

    @Override
    public boolean isAcceptingFriends() {
        return this.acceptFriends;
    }

    @Override
    public void setAcceptingFriends(boolean accept) {
        this.acceptFriends = accept;
    }

    @Override
    public boolean isAcceptingParties() {
        return acceptFriends;
    }

    @Override
    public boolean isShowingStatus() {
        return showStatus;
    }
    
}
