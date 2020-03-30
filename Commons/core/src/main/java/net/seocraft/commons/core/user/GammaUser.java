package net.seocraft.commons.core.user;

import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.partial.*;
import net.seocraft.api.core.user.partial.settings.GameSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GammaUser implements User {

    @NotNull private String id;
    @NotNull private String username;
    @NotNull private String display;
    @Nullable private String email;
    @NotNull private Set<GroupAssignation> groupAssignation;
    private String skin;
    @NotNull private SessionInfo sessionInfo;
    private boolean verified;
    private int level;
    private long experience;
    private List<IPRecord> ipRecord;
    @NotNull private String language;
    @NotNull private PublicInfo publicInfo;
    @NotNull private GameSettings gameSettings;
    @NotNull private Date memberSince;

    @ConstructorProperties({
            "_id", "username", "display", "email",
            "groups", "skin", "session",
            "verified", "level", "experience", "address", "language",
            "publicInfo", "settings", "createdAt"
    })
    public GammaUser(
            @NotNull String id, @NotNull String username, @NotNull String display, @Nullable String email,
            @NotNull Set<GroupAssignation> groupAssignation, String skin, @NotNull SessionInfo sessionInfo,
            boolean verified, int level, long experience, List<IPRecord> ipRecord, @NotNull String language,
            @NotNull PublicInfo publicInfo, @NotNull GameSettings gameSettings, @NotNull Date memberSince
    ) {
        this.id = id;
        this.username = username;
        this.display = display;
        this.email = email;
        this.groupAssignation = groupAssignation;
        this.skin = skin;
        this.sessionInfo = sessionInfo;
        this.verified = verified;
        this.level = level;
        this.experience = experience;
        this.ipRecord = ipRecord;
        this.language = language;
        this.publicInfo = publicInfo;
        this.gameSettings = gameSettings;
        this.memberSince = memberSince;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @Override
    public @NotNull String getUsername() {
        return this.username;
    }

    @Override
    public @NotNull String getDisplay() {
        return this.display;
    }

    @Override
    public @Nullable String getEmail() {
        return this.email;
    }

    @Override
    public @NotNull Set<GroupAssignation> getGroupAssignation() {
        return this.groupAssignation;
    }

    @Override
    public @NotNull Group getPrimaryGroup() {
        List<Group> groups = this.groupAssignation.stream().map(GroupAssignation::getGroup).collect(Collectors.toList());
        Group primaryGroup = groups.get(0);
        for (Group group: groups) {
            if ((group.getPriority() < primaryGroup.getPriority())) primaryGroup = group;
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
    public @NotNull SessionInfo getSessionInfo() {
        return this.sessionInfo;
    }

    @Override
    public @NotNull Date getMemberSince() {
        return this.memberSince;
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
    public @NotNull List<IPRecord> getUsedIp() {
        return this.ipRecord;
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
    public @NotNull PublicInfo getPublicInfo() {
        return this.publicInfo;
    }

    @Override
    public @NotNull GameSettings getGameSettings() {
        return this.gameSettings;
    }

}
