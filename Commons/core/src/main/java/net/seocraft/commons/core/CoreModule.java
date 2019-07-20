package net.seocraft.commons.core;

import com.google.inject.AbstractModule;
import net.seocraft.commons.core.server.ServerModule;

public class CoreModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ServerModule());
    }

}
