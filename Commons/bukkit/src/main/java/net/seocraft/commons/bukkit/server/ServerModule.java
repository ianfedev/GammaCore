package net.seocraft.commons.bukkit.server;

import com.google.inject.AbstractModule;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.server.ServerTokenQuery;

public class ServerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ServerLoad.class).to(BukkitServerLoad.class);
        bind(ServerTokenQuery.class).to(BukkitTokenQuery.class);
    }
}
