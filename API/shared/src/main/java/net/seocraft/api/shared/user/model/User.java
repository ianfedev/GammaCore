package net.seocraft.api.shared.user.model;

import net.seocraft.api.shared.model.Group;
import net.seocraft.api.shared.model.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface User extends Model {

    @NotNull String getUsername();

    @Nullable String getEmail();

    @NotNull List<Group> getGroups();

    void setGroups(List<Group> groups); //TODO: Create custom Gson deserializer

    @NotNull Group getPrimaryGroup();

    @NotNull String getSkin();

    void setSkin(@NotNull String skin);

    @NotNull Date getLastSeen();

    @NotNull String getLastGame();

    @NotNull Date getMemberSince();

    boolean isVerified();

    void setVerified(boolean verified);

    int getLevel();

    long getExperience();

    void addExperience(long experience);

    void removeExperience(long experience);

    @NotNull List<IpRecord> getUsedIp();

    boolean isDisguised();

    void setDisguised(boolean disguised);

    @Nullable String getDisguiseName();

    void setDisguiseName(@NotNull String name);

    @Nullable Group getDisguiseGroup();

    void setDisguiseGroup(@NotNull Group group);

    @Nullable List<DisguiseHistory> getDisguiseHistory();

    @NotNull String getLanguage();

    void setLanguage(@NotNull String language);

    boolean isAcceptingFriends();

    void setAcceptingFriends(boolean accept);

    boolean isAcceptingParties();

    boolean isShowingStatus();

    boolean isHiding();

    void setHiding(boolean hiding);

}