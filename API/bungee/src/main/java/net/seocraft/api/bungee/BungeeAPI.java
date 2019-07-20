package net.seocraft.api.bungee;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.md_5.bungee.api.plugin.Plugin;
import net.seocraft.api.core.SharedModule;

public class BungeeAPI extends Plugin {

    @Override
    public void onEnable() {
        Injector injector = Guice.createInjector(new SharedModule());
        injector.injectMembers(this);
    }


}
