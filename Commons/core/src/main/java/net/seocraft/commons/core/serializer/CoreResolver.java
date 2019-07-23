package net.seocraft.commons.core.serializer;

import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.group.partial.Flair;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.partial.Disguise;
import net.seocraft.api.core.user.partial.IPRecord;
import net.seocraft.commons.core.group.PermissionGroup;
import net.seocraft.commons.core.group.partial.MinecraftFlair;
import net.seocraft.commons.core.server.CoreServer;
import net.seocraft.commons.core.session.CraftSession;
import net.seocraft.commons.core.user.GammaUser;
import net.seocraft.commons.core.user.partial.DisguiseHistory;
import net.seocraft.commons.core.user.partial.PlayerIP;

public class CoreResolver {

    public static SimpleAbstractTypeResolver getCoreResolver() {
        return new SimpleAbstractTypeResolver()
                .addMapping(Flair.class, MinecraftFlair.class)
                .addMapping(Group.class, PermissionGroup.class)
                .addMapping(Server.class, CoreServer.class)
                .addMapping(GameSession.class, CraftSession.class)
                .addMapping(Disguise.class, DisguiseHistory.class)
                .addMapping(IPRecord.class, PlayerIP.class)
                .addMapping(User.class, GammaUser.class);
    }
}
