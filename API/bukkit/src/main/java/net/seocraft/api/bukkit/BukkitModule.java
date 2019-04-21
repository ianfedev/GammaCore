package net.seocraft.api.bukkit;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.seocraft.api.shared.SharedModule;

public class BukkitModule extends AbstractModule {

    private final BukkitAPI plugin;

    BukkitModule(BukkitAPI plugin){
        this.plugin = plugin;
    }

    Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        install(new SharedModule());
        this.bind(BukkitAPI.class).toInstance(this.plugin);
    }
}