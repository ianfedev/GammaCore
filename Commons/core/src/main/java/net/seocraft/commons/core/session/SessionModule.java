package net.seocraft.commons.core.session;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.session.MinecraftSessionManager;
import net.seocraft.api.core.session.MojangSessionValidation;

public class SessionModule extends ProtectedModule {

    @Override
    public void configure() {
        bind(MojangSessionValidation.class).to(MojangSessionValidator.class).in(Scopes.SINGLETON);
        bind(MinecraftSessionManager.class).to(GammaSessionManager.class).in(Scopes.SINGLETON);
    }
}
