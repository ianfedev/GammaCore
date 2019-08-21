package net.seocraft.commons.bukkit.user;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.user.UserFormatter;

public class UserModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(UserFormatter.class).to(GammaUserFormatter.class);
        expose(UserFormatter.class);
    }

}
