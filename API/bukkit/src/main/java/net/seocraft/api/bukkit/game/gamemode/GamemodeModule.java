package net.seocraft.api.bukkit.game.gamemode;

import com.google.inject.AbstractModule;

public class GamemodeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GamemodeHandler.class).to(GamemodeHandlerImp.class);
    }

}
