package net.seocraft.api.core.user;

import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.storage.Model;
import net.seocraft.api.core.user.partial.IPRecord;
import net.seocraft.api.core.user.partial.Disguise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface User extends Model {

    @NotNull String getUsername();

    @Nullable String getEmail();

    @NotNull List<Group> getGroups();

    void setGroups(List<Group> groups);

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

    @NotNull List<IPRecord> getUsedIp();

    boolean isDisguised();

    void setDisguised(boolean disguised);

    @Nullable String getDisguiseName();

    void setDisguiseName(@NotNull String name);

    @Nullable Group getDisguiseGroup();

    void setDisguiseGroup(@NotNull Group group);

    @Nullable List<Disguise> getDisguiseHistory();

    @NotNull String getLanguage();

    void setLanguage(@NotNull String language);

    boolean isAcceptingFriends();

    void setAcceptingFriends(boolean accept);

    boolean isAcceptingParties();

    boolean isShowingStatus();

    boolean isHiding();

    void setHiding(boolean hiding);

}