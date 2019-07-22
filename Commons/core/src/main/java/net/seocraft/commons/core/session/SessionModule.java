package net.seocraft.commons.core.session;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.session.GameSessionManager;

public class SessionModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(GameSessionManager.class).to(CraftSessionManager.class).in(Scopes.SINGLETON);
    }

}
