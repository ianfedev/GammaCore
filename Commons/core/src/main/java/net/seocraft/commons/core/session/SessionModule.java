package net.seocraft.commons.core.session;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.session.MinecraftSessionManager;

public class SessionModule extends ProtectedModule {

    @Override
    public void configure() {
        bind(MinecraftSessionManager.class).to(GammaSessionManager.class);
    }
}
