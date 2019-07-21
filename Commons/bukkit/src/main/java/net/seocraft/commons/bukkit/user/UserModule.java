package net.seocraft.commons.bukkit.user;

import com.google.inject.AbstractModule;
import net.seocraft.api.bukkit.user.UserFormatter;

public class UserModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserFormatter.class).to(GammaUserFormatter.class);
    }

}
