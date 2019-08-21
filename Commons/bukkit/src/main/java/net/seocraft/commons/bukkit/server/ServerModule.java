package net.seocraft.commons.bukkit.server;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.server.ServerTokenQuery;

public class ServerModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(ServerLoad.class).to(BukkitServerLoad.class);
        bind(ServerTokenQuery.class).to(BukkitTokenQuery.class);
        expose(ServerTokenQuery.class);
    }
}
