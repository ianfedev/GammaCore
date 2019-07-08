package net.seocraft.api.bukkit.game.map;

import com.google.inject.AbstractModule;

public class MapModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MapHandler.class).to(MapHandlerImp.class);
    }

}