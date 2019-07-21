package net.seocraft.commons.core.session;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.seocraft.api.core.session.GameSessionManager;

public class SessionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GameSessionManager.class).to(CraftSessionManager.class).in(Scopes.SINGLETON);
    }

}
