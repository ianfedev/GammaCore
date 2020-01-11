package net.seocraft.commons.core.user;

import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.partial.Disguise;
import net.seocraft.api.core.user.partial.IPRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;
import java.util.List;

public class GammaUser implements User {

    @NotNull private String id;
    @NotNull private String username;
    @Nullable private String email;
    private List<Group> groups;
    private String skin;
    private long lastSeen;
    private @NotNull String lastGame;
    private @NotNull String lastLobby;
    private long memberSince;
    private boolean verified;
    private int level;
    private long experience;
    private List<IPRecord> ipRecord;
    private boolean premium;
    private boolean disguised;
    @Nullable private String disguiseName;
    @Nullable private Group disguiseGroup;
    @Nullable private List<Disguise> disguiseHistory;
    @NotNull private String language;
    private boolean adminChatActive;
    private boolean acceptFriends;
    private boolean acceptParties;
    private boolean showStatus;
    private boolean hiding;

    @ConstructorProperties({"_id", "username", "email", "group", "skin", "last_seen", "last_game", "last_lobby", "member_since", "verified", "level", "experience",  "used_ips", "premium", "disguised", "disguise_actual", "disguise_group", "disguise_history", "language", "ac_active", "accept_friends", "accept_parties", "show_status", "hiding_players"})
    public GammaUser(@NotNull String id, @NotNull String username, @Nullable String email, List<Group> groups, String skin, long lastSeen, @NotNull String lastGame, @NotNull String lastLobby, long memberSince, boolean verified, int level, long experience, List<IPRecord> ipRecord, boolean premium, boolean disguised, @Nullable String disguiseName, @Nullable Group disguiseGroup, @Nullable List<Disguise> disguiseHistory, @NotNull String language, boolean adminChatActive, boolean acceptFriends, boolean acceptParties, boolean showStatus, boolean hiding) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.groups = groups;
        this.skin = skin;
        this.lastSeen = lastSeen;
        this.lastGame = lastGame;
        this.lastLobby = lastLobby;
        this.memberSince = memberSince;
        this.verified = verified;
        this.level = level;
        this.experience = experience;
        this.ipRecord = ipRecord;
        this.disguised = disguised;
        this.disguiseName = disguiseName;
        this.disguiseGroup = disguiseGroup;
        this.disguiseHistory = disguiseHistory;
        this.language = language;
        this.adminChatActive = adminChatActive;
        this.acceptFriends = acceptFriends;
        this.acceptParties = acceptParties;
        this.showStatus = showStatus;
        this.hiding = hiding;
    }

    public String getId() {
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
        Group primaryGroup = null;
        for (Group group: getGroups()) {
            if (
                    (primaryGroup != null && group.getPriority() < primaryGroup.getPriority()) ||
                    (group.getPriority() < 99999999)
            ) primaryGroup = group;
        }
        if (primaryGroup == null) primaryGroup = getGroups().get(0);
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
    public long getLastSeen() {
        return this.lastSeen;
    }

    @Override
    public @NotNull String getLastGame() {
        return this.lastGame;
    }

    @Override
    public @NotNull String getLastLobby() {
        return this.lastLobby;
    }

    @Override
    public long getMemberSince() {
        return this.memberSince;
    }

    @Override
    public boolean isVerified() {
        return this.verified;
    }

    @Override
    public boolean isPremium() {
        return this.premium;
    }

    @Override
    public void setPremium(boolean premium) {
        this.premium = premium;
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
    public @NotNull List<IPRecord> getUsedIp() {
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
    public @Nullable List<Disguise> getDisguiseHistory() {
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
    public boolean hasAdminChatActive() {
        return this.adminChatActive;
    }

    @Override
    public void setActiveChatActive(boolean accept) {
        this.adminChatActive = accept;
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
        return acceptParties;
    }

    @Override
    public boolean isShowingStatus() {
        return showStatus;
    }

    @Override
    public boolean isHiding() {
        return this.hiding;
    }

    @Override
    public void setHiding(boolean hiding) {
        this.hiding = hiding;
    }

}
