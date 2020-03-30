package net.seocraft.api.core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.storage.Model;
import net.seocraft.api.core.user.partial.*;
import net.seocraft.api.core.user.partial.settings.GameSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface User extends Model {

    @NotNull String getUsername();

    @NotNull String getDisplay();

    @Nullable String getEmail();

    @JsonProperty("groups")
    @NotNull Set<GroupAssignation> getGroupAssignation();

    @JsonIgnore
    @NotNull Group getPrimaryGroup();

    @NotNull String getSkin();

    void setSkin(@NotNull String skin);

    @NotNull SessionInfo getSessionInfo();

    boolean isVerified();

    void setVerified(boolean verified);

    int getLevel();

    long getExperience();

    @JsonIgnore
    void addExperience(long experience);

    @JsonIgnore
    void removeExperience(long experience);

    @JsonProperty("address")
    @NotNull List<IPRecord> getUsedIp();

    @NotNull String getLanguage();

    void setLanguage(@NotNull String language);

    @NotNull PublicInfo getPublicInfo();

    @JsonProperty("settings")
    @NotNull GameSettings getGameSettings();

    @JsonProperty("createdAt")
    @NotNull Date getMemberSince();

}