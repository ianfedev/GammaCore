package net.seocraft.commons.core.server;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.server.ServerManager;

public class CoreServerModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(ServerManager.class).to(CoreServerManager.class).in(Scopes.SINGLETON);
    }

}
