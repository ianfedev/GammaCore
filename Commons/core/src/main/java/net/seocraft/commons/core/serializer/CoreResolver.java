package net.seocraft.commons.core.serializer;

import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.group.partial.Flair;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.session.AuthValidation;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserExpulsion;
import net.seocraft.api.core.user.partial.Disguise;
import net.seocraft.api.core.user.partial.GroupAssignation;
import net.seocraft.api.core.user.partial.IPRecord;
import net.seocraft.api.core.user.partial.SessionInfo;
import net.seocraft.api.core.user.partial.settings.GameSettings;
import net.seocraft.api.core.user.partial.settings.partial.ACSettings;
import net.seocraft.api.core.user.partial.settings.partial.GeneralSettings;
import net.seocraft.commons.core.group.PermissionGroup;
import net.seocraft.commons.core.group.partial.MinecraftFlair;
import net.seocraft.commons.core.server.CoreServer;
import net.seocraft.commons.core.session.MinecraftAuthValidation;
import net.seocraft.commons.core.user.GammaUser;
import net.seocraft.commons.core.user.PlayerExpulsion;
import net.seocraft.commons.core.user.partial.*;
import net.seocraft.commons.core.user.partial.settings.UserGameSettings;
import net.seocraft.commons.core.user.partial.settings.partial.UserACSettings;
import net.seocraft.commons.core.user.partial.settings.partial.UserGeneralSettings;

public class CoreResolver {

    public static SimpleAbstractTypeResolver getCoreResolver() {
        return new SimpleAbstractTypeResolver()
                .addMapping(Flair.class, MinecraftFlair.class)
                .addMapping(Group.class, PermissionGroup.class)
                .addMapping(Server.class, CoreServer.class)
                .addMapping(Disguise.class, DisguiseHistory.class)
                .addMapping(IPRecord.class, PlayerIP.class)
                .addMapping(GroupAssignation.class, UserGroupAssignation.class)
                .addMapping(SessionInfo.class, UserSessionInfo.class)
                .addMapping(UserPublicInfo.class, UserPublicInfo.class)
                .addMapping(UserExpulsion.class, PlayerExpulsion.class)
                .addMapping(ACSettings.class, UserACSettings.class)
                .addMapping(GeneralSettings.class, UserGeneralSettings.class)
                .addMapping(GameSettings.class, UserGameSettings.class)
                .addMapping(AuthValidation.class, MinecraftAuthValidation.class)
                .addMapping(User.class, GammaUser.class);
    }
}
