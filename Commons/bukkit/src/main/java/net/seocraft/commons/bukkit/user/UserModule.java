package net.seocraft.commons.bukkit.user;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.commons.core.user.CoreUserModule;

public class UserModule extends ProtectedModule {

    @Override
    protected void configure() {
        install(new CoreUserModule());
        bind(UserFormatter.class).to(GammaUserFormatter.class);
    }

}
