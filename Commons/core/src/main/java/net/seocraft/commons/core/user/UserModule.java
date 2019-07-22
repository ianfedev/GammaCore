package net.seocraft.commons.core.user;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.user.UserStorageProvider;

public class UserModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(UserStorageProvider.class).to(GammaUserStorageProvider.class).in(Scopes.SINGLETON);
    }

}
