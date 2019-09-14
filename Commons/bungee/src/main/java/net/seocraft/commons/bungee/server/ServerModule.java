package net.seocraft.commons.bungee.server;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.server.ServerTokenQuery;

public class ServerModule extends ProtectedModule {

    @Override
    public void configure() {
        bind(ServerLoad.class).to(BungeeServerLoad.class);
        bind(ServerTokenQuery.class).to(BungeeTokenQuery.class);
        expose(ServerTokenQuery.class);
    }
}
