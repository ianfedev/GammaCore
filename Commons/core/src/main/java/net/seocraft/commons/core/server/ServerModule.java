package net.seocraft.commons.core.server;

import com.google.inject.AbstractModule;
import net.seocraft.api.core.server.ServerTokenQuery;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        requireBinding(ServerTokenQuery.class);
    }

}
