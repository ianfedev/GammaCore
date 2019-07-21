package net.seocraft.commons.core.server;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.api.core.server.ServerTokenQuery;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ServerManager.class).to(CoreServerManager.class).in(Scopes.SINGLETON);
        requireBinding(ServerTokenQuery.class);
    }

}
