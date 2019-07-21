package net.seocraft.commons.core.user;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.seocraft.api.core.user.UserStorageProvider;

public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserStorageProvider.class).to(GammaUserStorageProvider.class).in(Scopes.SINGLETON);
    }

}
