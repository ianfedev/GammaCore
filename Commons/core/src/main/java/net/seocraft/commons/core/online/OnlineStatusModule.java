package net.seocraft.commons.core.online;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.online.OnlineStatusManager;

public class OnlineStatusModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(OnlineStatusManager.class).to(CraftOnlineStatusManager.class);
    }

}
