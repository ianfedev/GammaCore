package net.seocraft.commons.bukkit.user;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.bukkit.user.UserLobbyMessageHandler;

public class UserModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(UserFormatter.class).to(GammaUserFormatter.class);
        bind(UserLobbyMessageHandler.class).to(GammaUserLobbyMessageHandler.class).in(Scopes.SINGLETON);
        expose(UserLobbyMessageHandler.class);
        expose(UserFormatter.class);
    }

}
