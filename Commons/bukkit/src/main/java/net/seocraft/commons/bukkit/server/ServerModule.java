package net.seocraft.commons.bukkit.server;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.commons.core.server.CoreServerModule;

public class ServerModule extends ProtectedModule {
    @Override
    protected void configure() {
        install(new CoreServerModule());
        bind(ServerLoad.class).to(BukkitServerLoad.class);
        bind(ServerTokenQuery.class).to(BukkitTokenQuery.class);
    }
}
