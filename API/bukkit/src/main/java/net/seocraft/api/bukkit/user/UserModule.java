package net.seocraft.api.bukkit.user;

import com.google.inject.AbstractModule;

public class UserModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserStoreHandler.class).to(UserStoreHandlerImp.class);
    }
}
