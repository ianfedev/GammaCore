package net.seocraft.api.bukkit.server;

import com.google.inject.AbstractModule;
import net.seocraft.api.bukkit.server.management.ServerLoad;
import net.seocraft.api.bukkit.server.management.ServerLoadImp;
import net.seocraft.api.bukkit.server.management.ServerManager;
import net.seocraft.api.bukkit.server.management.ServerManagerImp;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ServerLoad.class).to(ServerLoadImp.class);
        bind(ServerManager.class).to(ServerManagerImp.class);
    }

}
