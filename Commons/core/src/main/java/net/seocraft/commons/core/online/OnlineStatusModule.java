package net.seocraft.commons.core.online;

import com.google.inject.AbstractModule;
import net.seocraft.api.core.online.OnlineStatusManager;

public class OnlineStatusModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OnlineStatusManager.class).to(CraftOnlineStatusManager.class);
    }

}
