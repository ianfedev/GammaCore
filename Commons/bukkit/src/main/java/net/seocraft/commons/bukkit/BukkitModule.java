package net.seocraft.commons.bukkit;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BukkitModule extends AbstractModule {

    private final CommonsBukkit plugin;

    BukkitModule(CommonsBukkit plugin){
        this.plugin = plugin;
    }

    Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(CommonsBukkit.class).toInstance(this.plugin);
    }
}