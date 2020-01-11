package net.seocraft.api.core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.storage.Model;
import net.seocraft.api.core.user.partial.Disguise;
import net.seocraft.api.core.user.partial.IPRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface User extends Model {

    @NotNull String getUsername();

    @Nullable String getEmail();

    @JsonProperty("group")
    @NotNull List<Group> getGroups();

    void setGroups(List<Group> groups);

    @JsonIgnore
    @NotNull Group getPrimaryGroup();

    @NotNull String getSkin();

    void setSkin(@NotNull String skin);

    @JsonProperty("last_seen")
    long getLastSeen();

    @JsonProperty("last_game")
    @NotNull String getLastGame();

    @JsonProperty("last_lobby")
    @NotNull String getLastLobby();

    @JsonProperty("member_since")
    long getMemberSince();

    boolean isVerified();

    boolean isPremium();

    void setPremium(boolean premium);

    void setVerified(boolean verified);

    int getLevel();

    long getExperience();

    @JsonIgnore
    void addExperience(long experience);

    @JsonIgnore
    void removeExperience(long experience);

    @JsonProperty("used_ips")
    @NotNull List<IPRecord> getUsedIp();

    boolean isDisguised();

    void setDisguised(boolean disguised);

    @JsonProperty("disguise_actual")
    @Nullable String getDisguiseName();

    void setDisguiseName(@NotNull String name);

    @JsonProperty("disguise_group")
    @Nullable Group getDisguiseGroup();

    @JsonProperty("disguise_group")
    void setDisguiseGroup(@NotNull Group group);

    @JsonProperty("disguise_history")
    @Nullable List<Disguise> getDisguiseHistory();

    @NotNull String getLanguage();

    void setLanguage(@NotNull String language);

    @JsonProperty("ac_active")
    boolean hasAdminChatActive();

    @JsonProperty("ac_active")
    void setActiveChatActive(boolean accept);

    @JsonProperty("accept_friends")
    boolean isAcceptingFriends();

    @JsonProperty("accept_friends")
    void setAcceptingFriends(boolean accept);

    @JsonProperty("accept_parties")
    boolean isAcceptingParties();

    @JsonProperty("show_status")
    boolean isShowingStatus();

    @JsonProperty("hiding_players")
    boolean isHiding();

    @JsonProperty("hiding_players")
    void setHiding(boolean hiding);

}